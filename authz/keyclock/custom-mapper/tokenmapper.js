// https://www.bityard.org/blog/2022/04/21/keycloak_script_mapper

/**
 * The actual debug output function
 */
function debugOutput(msg) {
  print("Debug script mapper: " + msg);
}

// https://www.keycloak.org/docs-api/22.0.5/javadocs/org/keycloak/models/cache/infinispan/UserAdapter.html
// https://www.keycloak.org/docs-api/22.0.5/javadocs/org/keycloak/representations/AccessToken.html
// https://www.keycloak.org/docs-api/22.0.5/javadocs/org/keycloak/representations/JsonWebToken.html

debugOutput("Starting tokenmapper.js");
debugOutput("User: " + user);
var roles = [];
for (var client in token.getResourceAccess()) {
    debugOutput("Client: " + client);
    debugOutput("Roles: " + token.getResourceAccess().get(client).getRoles());
    roles = roles.concat(token.getResourceAccess().get(client).getRoles());
}
token.setResourceAccess(null);

// https://gist.github.com/webdeb/d8a99df9023f01b78e3e8ff580abe10b
debugOutput("Token releam access" + token.getRealmAccess());
token.setOtherClaims("p", roles);


// https://www.keycloak.org/docs-api/22.0.5/javadocs/org/keycloak/models/RoleModel.html
var roleMappings= user.getRoleMappingsStream().toArray();

// Iterate through roles
for (var i = 0; i < roleMappings.length; i++) {
  var role = roleMappings[i];
  debugOutput(role)
  var roleName = role.getName();
  debugOutput("role "+roleName)
}

// debugOutput("realm roles" + realmRoleNames)