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

import edu.internet2.middleware.subject.Subject;
import edu.internet2.middleware.subject.SubjectNotFoundException;
import edu.internet2.middleware.subject.SubjectNotUniqueException;

import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ESCOJNDISourceAdapter that add the notion of a context defined by an
 * attribute value.
 * 
 * @author GIP RECIA - A. Deman 1 oct. 2009
 */
public class ESCOJNDISourceAdapter extends JNDISourceAdapter {

    /** The scope delimitor in the search queries. */
    private static final String SCOPE_DELIM = "scope=";

    /** Separator used to separate the scope attribut values. */
    private static final String SCOPE_SEP = ";";

    /** Logger. */
    private static final Log LOGGER = LogFactory.getLog(ESCOJNDISourceAdapter.class);

    /**
     * Builds an instance of ESCOJNDISourceAdapter.
     */
    public ESCOJNDISourceAdapter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subject getSubject(final String subjectId, @SuppressWarnings("unused") final boolean exceptionIfNull)
            throws SubjectNotFoundException, SubjectNotUniqueException {
        Subject subject = null;
        Search search = this.getSearch("searchSubject");
        if (search == null) {
            LOGGER.error("searchType: \"searchSubject\" not defined.");
            return subject;
        }
        String[] attributeNames = {this.nameAttributeName, this.descriptionAttributeName,
                this.subjectIDAttributeName };
        Attributes subjAttributes = this.getLdapUnique(search, subjectId, attributeNames);
        subject = this.createSubject(subjAttributes);
        if (subject == null) {
            throw new SubjectNotFoundException("Subject " + subjectId + " not found.");
        }

        return subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Subject getSubjectByIdentifier(final String subjectId, final boolean exceptionIfNull)
            throws SubjectNotFoundException, SubjectNotUniqueException {
        Subject subject = null;
        Search search = this.getSearch("searchSubjectByIdentifier");
        if (search == null) {
            LOGGER.error("searchType: \"searchSubjectByIdentifier\" not defined.");
            return subject;
        }
        String[] attributeNames = {this.nameAttributeName, this.subjectIDAttributeName,
                this.descriptionAttributeName };
        Attributes subjectAttributes = this.getLdapUnique(search, subjectId, attributeNames);
        subject = this.createSubject(subjectAttributes);
        if (subject == null && exceptionIfNull) {
            throw new SubjectNotFoundException("Subject " + subjectId + " not found.");
        }
        return subject;
    }
    
    

    /**
     * {@inheritDoc}
     * @deprecated
     */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	public Subject getSubject(final String subjectId) throws SubjectNotFoundException, SubjectNotUniqueException {
        Subject subject = null;
        Search search = this.getSearch("searchSubject");
        if (search == null) {
            LOGGER.error("searchType: \"searchSubject\" not defined.");
            return subject;
        }
        String[] attributeNames = {this.nameAttributeName, this.descriptionAttributeName,
                this.subjectIDAttributeName };
        Attributes subjAttributes = this.getLdapUnique(search, subjectId, attributeNames);
        subject = this.createSubject(subjAttributes);
        if (subject == null) {
            throw new SubjectNotFoundException("Subject " + subjectId + " not found.");
        }

        return subject;
    }

    /**
     * {@inheritDoc}
     * @deprecated
     */
    @SuppressWarnings("deprecation")
	@Deprecated
	@Override
    public Subject getSubjectByIdentifier(final String subjectId) throws SubjectNotFoundException,
            SubjectNotUniqueException {
        Subject subject = null;
        Search search = this.getSearch("searchSubjectByIdentifier");
        if (search == null) {
            LOGGER.error("searchType: \"searchSubjectByIdentifier\" not defined.");
            return subject;
        }
        String[] attributeNames = {this.nameAttributeName, this.subjectIDAttributeName,
                this.descriptionAttributeName };
        Attributes subjectAttributes = this.getLdapUnique(search, subjectId, attributeNames);
        subject = this.createSubject(subjectAttributes);
        if (subject == null) {
            throw new SubjectNotFoundException("Subject " + subjectId + " not found.");
        }
        return subject;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set < Subject > search(final String searchString) {

        final Set < Subject > result = new HashSet < Subject >();
        Search search = this.getSearch("search");
        String searchExpression;

        // If an scope value is found in the search string
        // the string is decomposed and a decorated Search instance is used.
        final int index = searchString.indexOf(ESCOJNDISourceAdapter.SCOPE_DELIM);
        if (index >= 0) {
            final String searchTerm = searchString.substring(0, index).trim();
            final String scopeTerm = searchString.substring(index + ESCOJNDISourceAdapter.SCOPE_DELIM.length()).trim();
            final String[] scopes = scopeTerm.split(ESCOJNDISourceAdapter.SCOPE_SEP);
            search = new ESCOSearchWithScopeDecorator(scopes, search);
            searchExpression = searchTerm;
        } else {
            searchExpression = searchString;
        }

        if (search == null) {
            LOGGER.error("searchType: \"search\" not defined.");
            return result;
        }
        final String[] attributeNames = {this.nameAttributeName, this.subjectIDAttributeName,
                this.descriptionAttributeName, };

        @SuppressWarnings("rawtypes")
		NamingEnumeration ldapResults = this.getLdapResults(search, searchExpression, attributeNames);
        if (ldapResults == null) {
            return result;
        }
        try {
            while (ldapResults.hasMore()) {
                SearchResult si = (SearchResult) ldapResults.next();
                Attributes attributes1 = si.getAttributes();
                Subject subject = this.createSubject(attributes1);
                result.add(subject);
            }
        } catch (NamingException ex) {
            LOGGER.error("LDAP Naming Except: " + ex.getMessage(), ex);
        }

        return result;

    }

    /**
     * Creates a subject with a custom implementation.
     * 
     * @param attrs
     *            The attributes associated to the subject.
     * @return The Subject.
     */
    private Subject createSubject(final Attributes attrs) {
        String subjectName = "";
        String subjectID = "";
        String description = "";
        try {
            Attribute attribute = attrs.get(this.subjectIDAttributeName);
            if (attribute == null) {
                LOGGER.error("The LDAP attribute \"" + this.subjectIDAttributeName
                        + "\" does not have a value. "
                        + "It is beging used as the Grouper special attribute \"SubjectID\".");
                return null;
            }
            subjectID = (String) attribute.get();
            attribute = attrs.get(this.nameAttributeName);
            if (attribute == null) {
                LOGGER.error("The LDAP attribute \"" + this.nameAttributeName
                        + "\" does not have a value. z"
                        + "It is being used as the Grouper special attribute \"name\".");
                return null;
            }
            subjectName = (String) attribute.get();
            attribute = attrs.get(this.descriptionAttributeName);
            if (attribute == null) {
                LOGGER.error("The LDAP attribute \"" + this.descriptionAttributeName
                        + "\" does not have a value. "
                        + "It is being used as the Grouper special attribute \"description\".");
            } else {
                description = (String) attribute.get();
            }
        } catch (NamingException ex) {
            LOGGER.error("LDAP Naming Except: " + ex.getMessage(), ex);
        }

        return new ESCOJNDISubject(subjectID, subjectName, description, this.getSubjectType(), this);

    }
}
