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
                remark
                routes {
                    url
                    contextPath
                    enabled
                    remark
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


