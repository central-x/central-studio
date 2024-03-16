findById
===

* 根据主键查询数据

```graphql
query DatabaseProvider($id: String) {
    system {
        databases {
            findById(id: $id) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
query DatabaseProvider($ids: [String]) {
    system {
        databases {
            findByIds(ids: $ids){
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
query DatabaseProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    system {
        databases {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
query DatabaseProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    system {
        databases {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Database {
                        id
                        applicationId
                        application {
                            id
                            code
                            name
                        }
                        code
                        name
                        type
                        enabled
                        remark
                        master {
                            driver
                            url
                            username
                            password
                        }
                        slaves {
                            driver
                            url
                            username
                            password
                        }
                        params

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
query DatabaseProvider($conditions: [ConditionInput]) {
    system {
        databases {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation DatabaseProvider($input: DatabaseInput, $operator: String) {
    system {
        databases {
            insert(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
mutation DatabaseProvider($inputs: [DatabaseInput], $operator: String) {
    system {
        databases {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
mutation DatabaseProvider($input: DatabaseInput, $operator: String) {
    system {
        databases {
            update(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
mutation DatabaseProvider($inputs: [DatabaseInput], $operator: String) {
    system {
        databases {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                type
                enabled
                remark
                master {
                    driver
                    url
                    username
                    password
                }
                slaves {
                    driver
                    url
                    username
                    password
                }
                params

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
mutation DatabaseProvider($ids: [String]) {
    system {
        databases {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation DatabaseProvider($conditions: [ConditionInput]) {
    system {
        databases {
            deleteBy(conditions: $conditions)
        }
    }
}
```