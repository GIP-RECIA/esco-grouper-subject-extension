/**
 * 
 */
package edu.internet2.middleware.subject.provider;

import edu.internet2.middleware.subject.SubjectType;

import java.util.Set;


/**
 * Adds the support for the multi-valued attributes to the
 * getAttributeValue method.
 * @author GIP RECIA - A. Deman
 * 2 oct. 2009
 *
 */
public class ESCOJNDISubject extends JNDISubject {
    
    /** The separator for the values of a multi-valued attribute. */
    private static final String SEP = ";";

    /**
     * Builds an instance of ESCOJNDISubject.
     * @param id The id of the subject.
     * @param name The name of the subject.
     * @param description The description of the subject.
     * @param type The type of subject.
     * @param adapter The source adapter used to retrieve the subject.
     */
    public ESCOJNDISubject(final String id, final String name, final String description,
            final SubjectType type, final JNDISourceAdapter adapter) {
        super(id, name, description, type, adapter);
    }
    
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValue(final String attName) {
       
        @SuppressWarnings("unchecked")
        Set<String> values = getAttributeValues(attName);
        if (values == null) {
            return null;
        }
        String result = "";
        boolean separate = false;
        for (String value : values) {
            if (separate) {
                result +=  SEP;
            } else {
                separate = true;
            }
            result += value;
        }
       return result;
    }


}
