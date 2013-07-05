/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.insalyon.creatis.vip.query.server.dao.persistance;


import com.google.gwt.http.client.UrlBuilder;
import fr.insalyon.creatis.vip.query.client.bean.Query;
import fr.insalyon.creatis.vip.core.client.view.user.UserLevel;
import fr.insalyon.creatis.vip.core.client.view.util.CountryCode;
import fr.insalyon.creatis.vip.core.server.dao.DAOException;
import fr.insalyon.creatis.vip.core.server.dao.mysql.PlatformConnection;
import fr.insalyon.creatis.vip.core.server.dao.mysql.UserData;
import fr.insalyon.creatis.vip.query.client.bean.Parameter;
import fr.insalyon.creatis.vip.query.client.bean.QueryVersion;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.lang.Character;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;

/**
 *
 * @author Boujelben
 */
public class QueryData implements QueryDAO {
     private final static Logger logger = Logger.getLogger(QueryData.class);
     private Connection connection;

    public QueryData() throws DAOException {
        connection = PlatformConnection.getInstance().getConnection();
    }

    
    
    
    
    @Override
    public List<String[]> getQueries() throws DAOException {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT queryID, queryName FROM Query");
            ResultSet rs = ps.executeQuery();
             List<String[]> queries  = new ArrayList<String[]>();
            
            while (rs.next()) {
                
                int id=rs.getInt("queryID");
                
                 PreparedStatement ps2 = connection.prepareStatement("SELECT queryversionID, queryVersion, dateCreation FROM QueryVersion WHERE queryID=?");
                 
                
                 ps2.setInt(1,id);
                 ResultSet rs2 = ps2.executeQuery();
                
                while (rs2.next()) {
               Timestamp date=rs2.getTimestamp("dateCreation");
               
               
                    queries.add(new String[]{rs.getString("queryName"),date.toString(),rs2.getString("queryVersion"),rs2.getString("queryversionID")});
                }
                ps2.close();

            }
            ps.close();
            return queries;

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }
        
           
    @Override
    public List<String[]> getQuerie(Long queryversionid) throws DAOException {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT queryName, description, body FROM Query q, QueryVersion v WHERE q.queryID=v.queryID AND v.queryVersionID=? ");
            ps.setLong(1, queryversionid);
            ResultSet rs = ps.executeQuery();
      
             List<String[]> queries  = new ArrayList<String[]>();
            
            while (rs.next()) {
                    queries.add(new String[]{rs.getString("queryName"),rs.getString("description"),rs.getString("body")});
                }
              
            
            ps.close();
            return queries;

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }
      
  
        
@Override
    public List<String[]> getVersion() throws DAOException {
       try {
            PreparedStatement ps = connection.prepareStatement("SELECT "
                    + "queryName,dateCreation,queryVersion FROM"
                    + " Query query,QueryVersion queryversion "
                    +"query.queryID=queryVersion.queryID"
                    +"ORDER BY query.queryName");

            ResultSet rs = ps.executeQuery();
            List<String[]> queries = new ArrayList<String[]>();

            while (rs.next()) {
                queries.add(new String[]{rs.getString("queryName"), rs.getString("dateCreation"),rs.getString("queryVersion")});
            }
            ps.close();
            return queries;

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }
    
    
   
@Override
public void  removeVersion(Long versionid) throws DAOException {
           
        try {
            PreparedStatement ps = connection.prepareStatement("DELETE "
                    + "FROM QueryVersion WHERE queryVersionID=?");

           
            ps.setLong(1,versionid);
           
            ps.execute();
            ps.close();

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }
                      
                      

          
    @Override
        public List<Long> addParameter(Parameter param) throws DAOException {
             
         try {
           PreparedStatement pss=connection.prepareStatement("SELECT body from QueryVersion WHERE queryVersionID=? ");
           pss.setLong(1, param.getQueryVersionID());
          
          ResultSet rss=pss.executeQuery();
          String body=null;
           while (rss.next()){
           body=rss.getString("body");
           }
           //String str[]=body.split("\\[");
             List<String> listparam=new ArrayList<String>(); 
            char b='a';
            
            char last='a';
        
           for (int i=0;i<body.length();i++){
               b=body.charAt(i);
               if(b=='['){
                   int k=0;
               for (int j=i;j<body.length();j++){
                       last=body.charAt(j);
                       
               if(last==']'&& k==0){
               listparam.add(body.substring(i+1, j-1));
               k=1;
               
               }
                   }
           }
           }
            List <Long> parametersid=new ArrayList<Long>(); 
          for (String s:listparam)
          {
              String str[]=s.split("\\;");
              try
              {
               PreparedStatement ps = null;
                ps  = connection.prepareStatement(
                    "INSERT INTO Parameter(name, type, description, example, queryVersionID) "
                    + "VALUES (?, ?, ?, ?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
           ps.setString(1,str[0]);
           ps.setString(2,str[1]);
           ps.setString(3,str[2]);
           ps.setString(4,str[3]);
           ps.setLong(5,param.getQueryVersionID());
           ps.execute();
           ResultSet rs = ps.getGeneratedKeys();
            Long idAuto_increment= new Long(0);
            while (rs.next()){
            idAuto_increment = rs.getLong(1);
            }
             
            ps.close();
           
              parametersid.add(idAuto_increment);
              } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
              
          }
          
          
           
         
            return parametersid; 
       
    } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }

      
//methode ajoute un query et retourne un long l'id de la requete
@Override
    public Long add(Query query) throws DAOException {
        PreparedStatement ps = null;
         try {
            ps = connection.prepareStatement(
                    "INSERT INTO Query(description, queryName, queryMaker) "
                    + "VALUES (?, ?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, query.getDescription());
            ps.setString(2, query.getName());
            ps.setString(3, query.getQueryMaker());
          
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            Long idAuto_increment= new Long(0);
            while (rs.next()){
            idAuto_increment = rs.getLong(1);
            }
             
            ps.close();
            return idAuto_increment;
            

        } catch (SQLException ex) {
            //if (ex.getMessage().contains("Duplicate entry")) {
               // logger.error("An application named \"" + application.getName() + "\" already exists.");
                //throw new DAOException("An application named \"" + application.getName() + "\" already exists.");
           // } else {
                logger.error(ex);
                throw new DAOException(ex);
            }
         
        }
    


@Override
 public Long addVersion(QueryVersion version) throws DAOException {
         try {
           
            PreparedStatement ps2 = connection.prepareStatement(
                  "INSERT INTO QueryVersion(queryVersion, queryID, body, dateCreation) VALUES (?, ?, ?, ?)",PreparedStatement.RETURN_GENERATED_KEYS);
            ps2.setString(1,version.getQueryVersion());
            ps2.setObject(2, version.getQueryID());
            ps2.setString(3,version.getBody());
            ps2.setTimestamp(4,getCurrentTimeStamp());
            ps2.execute();
            ResultSet rs = ps2.getGeneratedKeys();
            Long idAuto_increment= new Long(0);
            while (rs.next()){
            idAuto_increment = rs.getLong(1);
            }
            
            ps2.close();
           return idAuto_increment;
        } catch (SQLException ex) {
            //if (ex.getMessage().contains("Duplicate entry")) {
               // logger.error("An application named \"" + application.getName() + "\" already exists.");
                //throw new DAOException("An application named \"" + application.getName() + "\" already exists.");
           // } else {
                logger.error(ex);
                throw new DAOException(ex);
            }
         
        }


 @Override
    public List<Parameter> getParameter(Long queryVersionID) throws DAOException {

        try {
            PreparedStatement ps = connection.prepareStatement("SELECT "
                    + "parameterID, name, type, description, example"
                    + "FROM Parameter "
                    + "WHERE queryVersionID = ?");

            ps.setLong(1, queryVersionID);
            ResultSet rs = ps.executeQuery();
            List<Parameter> parameters = new ArrayList<Parameter>();

            while (rs.next()) {
               parameters .add(new Parameter(rs.getLong("parameterID"), rs.getString("name"),rs.getString("type"),rs.getString("description"),rs.getString("example")));
            }

            ps.close();
            return parameters;

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }


    /*
      @Override
    public void updateVersion(Long versionid, String description, String queryName ) throws DAOException {

        try {
            PreparedStatement ps = connection.prepareStatement("UPDATE "
                    + "query q, queryversion v "
                    + "SET query.description=? "
                    + "SET query.queryName=? "
                    //+ "SET queryversion.body=? "
                    //+ "SET queryversion.dateCreation=? "
                    + "WHERE queryVersionID=?");

            ps.setString(1, application.getCitation());
            ps.setString(2, application.getName());
            ps.executeUpdate();
            ps.close();

            removeAllClassesFromApplication(application.getName());
            for (String className : application.getApplicationClasses()) {
                addClassToApplication(application.getName(), className);
            }

        } catch (SQLException ex) {
            logger.error(ex);
            throw new DAOException(ex);
        }
    }
*/
    
      private static java.sql.Timestamp getCurrentTimeStamp() {
 
		java.util.Date today = new java.util.Date();
		return new java.sql.Timestamp(today.getTime());
 
	}
      
      
      
      
      
      
    }
        
    
    

    
    
    
    
