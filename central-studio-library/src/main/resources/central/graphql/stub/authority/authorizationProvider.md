findApplication
===

* 权限查询

```graphql
query AuthorizationProvider($code: String, $secret: String) {
    authority {
        authorizations {
            findApplication(code: $code, secret: $secret) {
                id
                code
                name
                logo
                url
                contextPath
                secret
                enabled

                creatorId
                createDate
                creator {
                    id
                    username
                    name
                }

                modifierId
                modifyDate
                modifier {
                    id
                    username
                    name
                }
            }
        }
    }
}
```

findApplications
===

* 获取指定帐户允许访问的应用列表

```graphql
query AuthorizationProvider($accountId: String, $type: String) {
    authority {
        authorizations {
            findApplications(accountId: $accountId, type: $type) {
                id
                code
                name
                logo
                url
                contextPath
                secret
                enabled
                remark

                creatorId
                createDate
                creator {
                    id
                    username
                    name
                }

                modifierId
                modifyDate
                modifier {
                    id
                    username
                    name
                }
            }
        }
    }
}
```

findRoles
===

* 获取指定帐户在指定应用下被授权的角色清单

```graphql
query AuthorizationProvider($accountId: String, $applicationId: String) {
    authority {
        authorizations {
            findRoles(accountId: $accountId, applicationId: $applicationId) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                creatorId
                createDate
                creator {
                    id
                    username
                    name
                }

                modifierId
                modifyDate
                modifier {
                    id
                    username
                    name
                }
            }
        }
    }
}
```

findMenus
===

* 获取指定帐户在指定应用下被授权的菜单清单

```graphql
query AuthorizationProvider($accountId: String, $type: String, $applicationId: String) {
    authority {
        authorizations {
            findMenus(accountId: $accountId, type: $type, applicationId: $applicationId) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                icon
                url
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                }

                creatorId
                createDate
                creator {
                    id
                    username
                    name
                }

                modifierId
                modifyDate
                modifier {
                    id
                    username
                    name
                }
            }
        }
    }
}
```

findPermissions
===

* 获取指定帐户在指定应用下被授权的权限列表

```graphql
query AuthorizationProvider($accountId: String, $applicationId: String) {
    authority {
        authorizations {
            findPermissions(accountId: $accountId, applicationId: $applicationId) {
                id
                applicationId
                menuId
                code
                name

                creatorId
                createDate
                creator {
                    id
                    username
                    name
                }

                modifierId
                modifyDate
                modifier {
                    id
                    username
                    name
                }
            }
        }
    }
}
```