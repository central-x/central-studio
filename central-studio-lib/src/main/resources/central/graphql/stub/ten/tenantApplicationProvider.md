findById
===

* 根据主键查询数据

```graphql
query TenantApplicationProvider($id: String) {
    ten {
        tenants {
            applications {
                findById(id: $id) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

findByIds
===

* 根据主键查询数据

```graphql
query TenantApplicationProvider($ids: [String]) {
    ten {
        tenants {
            applications {
                findByIds(ids: $ids) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

findBy
===

* 根据条件查询数据

```graphql
query TenantApplicationProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    ten {
        tenants {
            applications {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

pageBy
===

* 分页查询数据

```graphql
query TenantApplicationProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    ten {
        tenants {
            applications {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on TenantApplication {
                            id
                            tenantId
                            tenant {
                                id
                                code
                                name
                                enabled
                            }
                            applicationId
                            application {
                                id
                                code
                                name
                                logo
                                url
                                contextPath
                                secret
                                enabled
                            }
                            enabled
                            primary

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
}
```

countBy
===

* 查询符合条件的数据数量

```graphql
query TenantApplicationProvider($conditions: [ConditionInput]) {
    ten {
        tenants {
            applications {
                countBy(conditions: $conditions)
            }
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation TenantApplicationProvider($input: TenantApplicationInput, $operator: String) {
    ten {
        tenants {
            applications {
                insert(input: $input, operator: $operator) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

insertBatch
===

* 批量保存数据

```graphql
mutation TenantApplicationProvider($inputs: [TenantApplicationInput], $operator: String) {
    ten {
        tenants {
            applications {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

update
===

* 更新数据

```graphql
mutation TenantApplicationProvider($input: TenantApplicationInput, $operator: String) {
    ten {
        tenants {
            applications {
                update(input: $input, operator: $operator) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

updateBatch
===

* 批量更新数据

```graphql
mutation TenantApplicationProvider($inputs: [TenantApplicationInput], $operator: String) {
    ten {
        tenants {
            applications {
                updateBatch(inputs: $inputs, operator: $operator) {
                    id
                    tenantId
                    tenant {
                        id
                        code
                        name
                        enabled
                    }
                    applicationId
                    application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        secret
                        enabled
                    }
                    enabled
                    primary

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
```

deleteByIds
===

* 删除数据

```graphql
mutation TenantApplicationProvider($ids: [String]) {
    ten {
        tenants {
            applications {
                deleteByIds(ids: $ids)
            }
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation TenantApplicationProvider($conditions: [ConditionInput]) {
    ten {
        tenants {
            applications {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```