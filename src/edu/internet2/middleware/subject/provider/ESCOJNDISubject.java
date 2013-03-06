/**
 * Copyright (C) 2009 GIP RECIA http://www.recia.fr
 * @Author (C) 2009 GIP RECIA <contact@recia.fr>
 * @Contributor (C) 2009 SOPRA http://www.sopragroup.com/
 * @Contributor (C) 2011 Pierre Legay <pierre.legay@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.internet2.middleware.subject.provider;

import edu.internet2.middleware.subject.SubjectType;
import java.util.Set;

/**
 * Adds the support for the multi-valued attributes to the getAttributeValue
 * method.
 * 
 * @author GIP RECIA - A. Deman 2 oct. 2009
 */
public class ESCOJNDISubject extends JNDISubject {

    /**
     * The default generated uid.
     */
    private static final long   serialVersionUID = 6687997365345098962L;

    /** The separator for the values of a multi-valued attribute. */
    private static final String SEP              = ";";

    /**
     * Builds an instance of ESCOJNDISubject.
     * @param id The id of the subject.
     * @param name The name of the subject.
     * @param description The description of the subject.
     * @param type The type of subject.
     * @param adapter The source adapter used to retrieve the subject.
     */
    public ESCOJNDISubject(final String id, final String name, final String description, final SubjectType type,
            final JNDISourceAdapter adapter) {
        super(id, name, description, type.getName(), adapter.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAttributeValue(final String attName) {

        Set < String > values = this.getAttributeValues(attName);
        if (values == null) {
            return null;
        }
        String result = "";
        boolean separate = false;
        for (String value : values) {
            if (separate) {
                result += ESCOJNDISubject.SEP;
            } else {
                separate = true;
            }
            result += value;
        }
        return result;
    }

}
