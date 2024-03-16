findById
===

* 根据主键查询数据

```graphql
query AccountUnitProvider($id: String) {
    organization {
        accounts {
            units {
                findById(id: $id) {
                    id
                    accountId
                    account {
                        id
                        username
                        email
                        mobile
                        name
                    }
                    unitId
                    unit {
                        id
                        areaId
                        parentId
                        code
                        name
                        order
                    }
                    rankId
                    rank {
                        id
                        unitId
                        code
                        name
                        order
                    }
                    primary

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
query AccountUnitProvider($ids: [String]) {
    organization {
        accounts {
            units {
                findByIds(ids: $ids) {
                    id
                    accountId
                    account {
                        id
                        username
                        email
                        mobile
                        name
                    }
                    unitId
                    unit {
                        id
                        areaId
                        parentId
                        code
                        name
                        order
                    }
                    rankId
                    rank {
                        id
                        unitId
                        code
                        name
                        order
                    }
                    primary

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
query AccountUnitProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            units {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    accountId
                    account {
                        id
                        username
                        email
                        mobile
                        name
                    }
                    unitId
                    unit {
                        id
                        areaId
                        parentId
                        code
                        name
                        order
                    }
                    rankId
                    rank {
                        id
                        unitId
                        code
                        name
                        order
                    }
                    primary

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
query AccountUnitProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            units {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on AccountUnit {
                            id
                            accountId
                            account {
                                id
                                username
                                email
                                mobile
                                name
                            }
                            unitId
                            unit {
                                id
                                areaId
                                parentId
                                code
                                name
                                order
                            }
                            rankId
                            rank {
                                id
                                unitId
                                code
                                name
                                order
                            }
                            primary

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
query AccountUnitProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            units {
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
mutation AccountUnitProvider($input: AccountUnitInput, $operator: String) {
    organization {
        accounts {
            units {
                insert(input: $input, operator: $operator) {
                    id
                    accountId
                    account {
                        id
                        username
                        email
                        mobile
                        name
                    }
                    unitId
                    unit {
                        id
                        areaId
                        parentId
                        code
                        name
                        order
                    }
                    rankId
                    rank {
                        id
                        unitId
                        code
                        name
                        order
                    }
                    primary
                    
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
mutation AccountUnitProvider($inputs: [AccountUnitInput], $operator: String) {
    organization {
        accounts {
            units {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    accountId
                    account {
                        id
                        username
                        email
                        mobile
                        name
                    }
                    unitId
                    unit {
                        id
                        areaId
                        parentId
                        code
                        name
                        order
                    }
                    rankId
                    rank {
                        id
                        unitId
                        code
                        name
                        order
                    }
                    primary

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
mutation AccountUnitProvider($ids: [String]) {
    organization {
        accounts {
            units {
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
mutation AccountUnitProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            units {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```