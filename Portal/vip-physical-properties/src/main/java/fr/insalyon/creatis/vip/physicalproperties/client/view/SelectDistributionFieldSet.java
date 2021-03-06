/*
 * Copyright and authors: see LICENSE.txt in base repository.
 *
 * This software is a web portal for pipeline execution on distributed systems.
 *
 * This software is governed by the CeCILL-B license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-B
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-B license and that you accept its terms.
 */
package fr.insalyon.creatis.vip.physicalproperties.client.view;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.SimpleStore;
import com.gwtext.client.data.Store;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.form.ComboBox;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.MultiFieldPanel;
import com.gwtext.client.widgets.form.TextField;
import com.gwtext.client.widgets.form.event.ComboBoxListenerAdapter;
import fr.insalyon.creatis.vip.physicalproperties.client.bean.Distribution;
import fr.insalyon.creatis.vip.physicalproperties.client.bean.DistributionInstance;
import fr.insalyon.creatis.vip.physicalproperties.client.bean.DistributionParameter;
import fr.insalyon.creatis.vip.physicalproperties.client.bean.DistributionParameterValue;
import fr.insalyon.creatis.vip.physicalproperties.client.rpc.TissueService;
import fr.insalyon.creatis.vip.physicalproperties.client.rpc.TissueServiceAsync;
import fr.insalyon.creatis.vip.common.client.view.FieldUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author glatard
 */
public class SelectDistributionFieldSet extends FieldSet {

    private Store distributionStore;
    
    private Distribution currentDist;
    private DistributionInstance currentInstance;

    private HashMap distributions;
    private int previousNParam = 0;
    private MultiFieldPanel valuesMp;

    String title;


    ComboBox cb;
    
    public SelectDistributionFieldSet(String title) {
        super(title);
        this.title = title;

        currentDist = new Distribution();
        currentInstance = null;
        
        distributionStore = new SimpleStore("distribution-name2", new Object[]{});
        distributionStore.load();
        loadDistributions();

        cb = new ComboBox();
        cb.setFieldLabel("Distribution");
        cb.setStore(distributionStore);
        cb.setTypeAhead(true);
        cb.setMode(ComboBox.LOCAL);
        cb.setTriggerAction(ComboBox.ALL);
        cb.setDisplayField("distribution-name2");
        cb.setValueField("distribution-name2");
        cb.setWidth(150);
        cb.setReadOnly(true);
        cb.setEmptyText("Select a Distribution...");
        cb.addListener(new ComboBoxListenerAdapter() {

            @Override
            public void onSelect(ComboBox comboBox, Record record, int index) {
                previousNParam = currentDist.getParameters().size();
                currentDist = (Distribution) distributions.get(comboBox.getValueAsString());
                showParams();
            }
        });
        this.add(cb);

    }

    public Distribution getCurrentDist() {
        return currentDist;
    }

    public MultiFieldPanel getValuesMp() {
        return valuesMp;
    }

    private void loadDistributions() {
        TissueServiceAsync ts = TissueService.Util.getInstance();

        final AsyncCallback<List<Distribution>> callback = new AsyncCallback<List<Distribution>>() {

            public void onFailure(Throwable caught) {
                MessageBox.alert("Error", "Error getting distribution list\n" + caught.getMessage());
            }

            public void onSuccess(List<Distribution> result) {
                distributions = new HashMap();
                Object[][] data = new Object[result.size()][1];
                for (int i = 0; i < result.size(); i++) {
                    data[i][0] = ((Distribution) result.get(i)).getName();
                    distributions.put(data[i][0], ((Distribution) result.get(i)));
                }
                MemoryProxy proxy = new MemoryProxy(data);
                distributionStore.setDataProxy(proxy);
                distributionStore.load();
                distributionStore.commitChanges();
            }
        };
        ts.getDistributions(callback);
    }
    public void clearParams(){
        currentInstance = null;
    }
    private void showParams() {
        TextField t = null;
        valuesMp = null;
        final int n = currentDist.getParameters().size();
        for (int i = 0; i < previousNParam; i++) {
            this.remove(title + "-mp-" + i);
        }
        int i = 0;
        for (DistributionParameter p : currentDist.getParameters()) {
            //distField.remove("param-" + i);
//            t = FieldUtil.getTextField(title + "-param-" + i, 300, p.getName()+" ("+p.getSymbol()+")", false);
            if(currentInstance != null)
            {
                for(DistributionParameterValue val : currentInstance.getValues())
                {
                        if(val.getParam().getName().equals(p.getName())){
                           t.setValue(""+val.getValue());
                        }
                }

            }
//            valuesMp = FieldUtil.getMultiFieldPanel(title+"-mp-"+i);
            valuesMp.add(t);
            //t.setValue(p.getName());
            this.add(valuesMp);
            i++;
        }
        this.doLayout();
    }

    public void setDistributionInstance(DistributionInstance inst){
        currentInstance = inst;
        currentDist = currentInstance.getDistributionType();
        cb.setValue(currentDist.getName());
        showParams();
    }

    public void parseInstance() {
        List<DistributionParameterValue> map = new ArrayList<DistributionParameterValue>();
        int i = 0;
        //MessageBox.alert("dfs-a");
        if (currentDist == null) {
            MessageBox.alert("currentdist is null !");
        }
        for (DistributionParameter p : currentDist.getParameters()) {
           // MessageBox.alert("dfs-before-" + i);
            MultiFieldPanel mfp = (MultiFieldPanel) this.getComponent(title + "-mp-" + i);
            //MessageBox.alert("dfs-before-tf" + i);
            TextField tf = (TextField) (mfp).getComponent(title + "-param-" + i);
           // MessageBox.alert("dfs-before-value" + i);
            String value = tf.getValueAsString();
           // MessageBox.alert("dfs-before-put-" + i);
            map.add(new DistributionParameterValue(p, Double.parseDouble(value)));
            i++;
           // MessageBox.alert("dfs-" + i);
        }
        DistributionInstance di = new DistributionInstance(-1, currentDist, map);
        //MessageBox.alert("dfs-z");

        currentInstance = di;

    }

    public DistributionInstance getDistributionInstance() {
        parseInstance();
        return currentInstance;
    }
}
