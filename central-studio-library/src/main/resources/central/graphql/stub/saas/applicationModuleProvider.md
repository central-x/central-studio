findById
===

* 根据主键查询数据

```graphql
query ApplicationModuleProvider($id: String) {
    saas {
        applications {
            modules {
                findById(id: $id) {
                    id
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
                    url
                    contextPath
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
}
```

findByIds
===

* 根据主键查询数据

```graphql
query ApplicationModuleProvider($ids: [String]) {
    saas {
        applications {
            modules {
                findByIds(ids: $ids) {
                    id
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
                    url
                    contextPath
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
}
```

findBy
===

* 根据条件查询数据

```graphql
query ApplicationModuleProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    saas {
        applications {
            modules {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
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
                    url
                    contextPath
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
}
```

pageBy
===

* 分页查询数据

```graphql
query ApplicationModuleProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    saas {
        applications {
            modules {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on ApplicationModule {
                            id
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
                            url
                            contextPath
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
        }
    }
}
```

countBy
===

* 查询符合条件的数据数量

```graphql
query ApplicationModuleProvider($conditions: [ConditionInput]) {
    saas {
        applications {
            modules {
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
mutation ApplicationModuleProvider($input: ApplicationModuleInput, $operator: String) {
    saas {
        applications {
            modules {
                insert(input: $input, operator: $operator) {
                    id
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
                    url
                    contextPath
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
}
```

insertBatch
===

* 批量保存数据

```graphql
mutation ApplicationModuleProvider($inputs: [ApplicationModuleInput], $operator: String) {
    saas {
        applications {
            modules {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
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
                    url
                    contextPath
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
}
```

update
===

* 更新数据

```graphql
mutation ApplicationModuleProvider($input: ApplicationModuleInput, $operator: String) {
    saas {
        applications {
            modules {
                update(input: $input, operator: $operator) {
                    id
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
                    url
                    contextPath
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
}
```

updateBatch
===

* 批量更新数据

```graphql
mutation ApplicationModuleProvider($inputs: [ApplicationModuleInput], $operator: String) {
    saas {
        applications {
            modules {
                updateBatch(inputs: $inputs, operator: $operator) {
                    id
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
                    url
                    contextPath
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
}
```

deleteByIds
===

* 删除数据

```graphql
mutation ApplicationModuleProvider($ids: [String]) {
    saas {
        applications {
            modules {
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
mutation ApplicationModuleProvider($conditions: [ConditionInput]) {
    saas {
        applications {
            modules {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```