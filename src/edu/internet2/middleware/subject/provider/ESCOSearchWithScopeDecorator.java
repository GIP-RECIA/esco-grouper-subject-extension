package edu.internet2.middleware.subject.provider;

/**
 * Decorator that handles a scope for the search operations.
 * This can be used, for instance to retrieve all the users that 
 * belong to an establishment.
 * @author GIP RECIA - A. Deman
 * 1 oct. 2009
 *
 */
public class ESCOSearchWithScopeDecorator extends Search {
    /** Parameter for the scope attribut. */
    private static final String SCOPE_PARAMETER = "scope-attribut";


    /** The key for the decorated. */ 
    private static final String FILTER_PARAM = "filter";

    /** The scopes to condider. */
    private String[] scopes;
    /**
     * Builds an instance of SearchWithScopeDecorator.
     * @param scopes The values of the scope.
     * @param decorated The search instance to decorate to handle the scope.
     */
    public ESCOSearchWithScopeDecorator(final String[] scopes, final Search decorated) {
        this.scopes = scopes;
        this.params = decorated.getParams();
    }              

    /**
     * Decorates the getParam method for the filter.
     * @param paramName The name of the parameter the retrieve.
     * @return The value of the parameter. It the requested parameter is the filter,
     * then it is extended to handles a scope attribute.
     * @see edu.internet2.middleware.subject.provider.Search#getParam(java.lang.String)
     */
    @Override
    protected String getParam(final String paramName) {

        // The requested param is the filter, so it is exetended 
        // to handles the scope.
        if (FILTER_PARAM.equals(paramName)) {
            final StringBuilder scopePartBldr = new StringBuilder();
            final String scopeAttribut = super.getParam(SCOPE_PARAMETER);
            // Only one value for the scope.
            if (scopes.length == 1) {
                scopePartBldr.append("(");
                scopePartBldr.append(scopeAttribut);
                scopePartBldr.append("=");
                scopePartBldr.append(scopes[0]);
                scopePartBldr.append("))");

            } else {
                // Several values in the scope.
                scopePartBldr.append("(|");
                for (String scope : scopes) {
                    scopePartBldr.append("(");
                    scopePartBldr.append(scopeAttribut);
                    scopePartBldr.append("=");
                    scopePartBldr.append(scope);
                    scopePartBldr.append(")");
                }
                scopePartBldr.append(")");
            }
            final StringBuilder filterBldr = new StringBuilder("(&");

            filterBldr.append(super.getParam(paramName));
            filterBldr.append(scopePartBldr);
            filterBldr.append(")");
            return filterBldr.toString();
        }
        // The other parameters are not changed.
        return super.getParam(paramName);
    }
}