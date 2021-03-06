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
package fr.insalyon.creatis.vip.simulationgui.client.util.math;

/**
 * Represents a vector with two elements.
 * 
 * @author Sönke Sothmann
 * @author Steffen Schäfer
 * 
 */
public class Vector2f implements Vectorf {

    private float u;
    private float v;

    /**
     * Constructs a new instance of the Vector2f with the given coordinates to
     * set.
     * 
     * @param u
     * @param v
     */
    public Vector2f(float u, float v) {
        super();
        this.u = u;
        this.v = v;
    }

    /**
     * Returns the u coordinate.
     * 
     * @return the u coordinate
     */
    public float getU() {
        return u;
    }

    /**
     * Sets the u coordinate.
     * 
     * @param u
     *            the u coordinate to set
     */
    public void setU(float u) {
        this.u = u;
    }

    /**
     * Returns the v coordinate.
     * 
     * @return the v coordinate
     */
    public float getV() {
        return v;
    }

    /**
     * Sets the v coordinate.
     * 
     * @param v
     *            the v coordinate to set
     */
    public void setV(float v) {
        this.v = v;
    }

    /* (non-Javadoc)
     * @see com.googlecode.gwtgl.example.client.util.math.Vector#multiply(float)
     */
    @Override
    public Vectorf multiply(float scalar) {
        return new Vector2f(this.u * scalar, this.v * scalar);
    }

    /* (non-Javadoc)
     * @see com.googlecode.gwtgl.example.client.util.math.Vector#length()
     */
    @Override
    public float length() {
        return (float) Math.sqrt(this.u * this.u + this.v * this.v);
    }

    /* (non-Javadoc)
     * @see com.googlecode.gwtgl.example.client.util.math.Vector#toUnitVector()
     */
    @Override
    public Vectorf toUnitVector() {
        float length = length();
        return new Vector2f(this.u / length, this.v / length);
    }

    /* (non-Javadoc)
     * @see com.googlecode.gwtgl.example.client.util.math.Vector#toArray()
     */
    @Override
    public float[] toArray() {
        return new float[]{this.u, this.v};
    }
}
