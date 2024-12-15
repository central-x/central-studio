findById
===

* 根据主键查询数据

```graphql
query RoleRangeProvider($id: String) {
    authority {
        roles {
            ranges {
                findById(id: $id) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    category
                    type
                    dataId
                    
                    creatorId
                    createDate
                    creator {
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
query RoleRangeProvider($ids: [String]) {
    authority {
        roles {
            ranges {
                findByIds(ids: $ids){
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    category
                    type
                    dataId

                    creatorId
                    createDate
                    creator {
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
query RoleRangeProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            ranges {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    category
                    type
                    dataId

                    creatorId
                    createDate
                    creator {
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
query RoleRangeProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            ranges {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on RoleRange {
                            id
                            applicationId
                            application {
                                id
                                code
                                name
                            }
                            roleId
                            role {
                                id
                                code
                                name
                            }
                            category
                            type
                            dataId

                            creatorId
                            createDate
                            creator {
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
query RoleRangeProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            ranges {
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
mutation RoleRangeProvider($input: RoleRangeInput, $operator: String) {
    authority {
        roles {
            ranges {
                insert(input: $input, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    category
                    type
                    dataId

                    creatorId
                    createDate
                    creator {
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
mutation RoleRangeProvider($inputs: [RoleRangeInput], $operator: String) {
    authority {
        roles {
            ranges {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    category
                    type
                    dataId

                    creatorId
                    createDate
                    creator {
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
mutation RoleRangeProvider($ids: [String]) {
    authority {
        roles {
            ranges {
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
mutation RoleRangeProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            ranges {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```