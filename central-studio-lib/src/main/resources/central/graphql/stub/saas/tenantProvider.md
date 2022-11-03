findById
===

* 根据主键查询数据

```graphql
query TenantProvider($id: String) {
    saas {
        tenants {
            findById(id: $id) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

findByIds
===

* 根据主键查询数据

```graphql
query TenantProvider($ids: [String]) {
    saas {
        tenants {
            findByIds(ids: $ids){
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

findBy
===

* 根据条件查询数据

```graphql
query TenantProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    saas {
        tenants {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

pageBy
===

* 分页查询数据

```graphql
query TenantProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    saas {
        tenants {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Tenant {
                        id
                        code
                        name
                        databaseId
                        database {
                            id
                            code
                            name
                            type
                            enabled
                            remark
                        }
                        enabled
                        remark
                        applications {
                            id
                            tenantId
                            applicationId
                            enabled
                            primary
                            application {
                                id
                                code
                                name
                                url
                                contextPath
                                secret
                                enabled
                                modules {
                                    id
                                    applicationId
                                    url
                                    contextPath
                                    enabled
                                }
                            }
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
    }
}
```

countBy
===

* 查询符合条件的数据数量

```graphql
query TenantProvider($conditions: [ConditionInput]) {
    saas {
        tenants {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation TenantProvider($input: TenantInput, $operator: String) {
    saas {
        tenants {
            insert(input: $input, operator: $operator) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

insertBatch
===

* 批量保存数据

```graphql
mutation TenantProvider($inputs: [TenantInput], $operator: String) {
    saas {
        tenants {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

update
===

* 更新数据

```graphql
mutation TenantProvider($input: TenantInput, $operator: String) {
    saas {
        tenants {
            update(input: $input, operator: $operator) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

updateBatch
===

* 批量更新数据

```graphql
mutation TenantProvider($inputs: [TenantInput], $operator: String) {
    saas {
        tenants {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                databaseId
                database {
                    id
                    code
                    name
                    type
                    enabled
                    remark
                }
                enabled
                remark
                applications {
                    id
                    tenantId
                    applicationId
                    enabled
                    primary
                    application {
                        id
                        code
                        name
                        url
                        contextPath
                        secret
                        enabled
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                        }
                    }
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

deleteByIds
===

* 删除数据

```graphql
mutation TenantProvider($ids: [String]) {
    saas {
        tenants {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation TenantProvider($conditions: [ConditionInput]) {
    saas {
        tenants {
            deleteBy(conditions: $conditions)
        }
    }
}
```