findById
===

* 根据主键查询数据

```graphql
query RankProvider($id: String) {
    organization {
        ranks {
            findById(id: $id) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
query RankProvider($ids: [String]) {
    organization {
        ranks {
            findByIds(ids: $ids){
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
query RankProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        ranks {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
query RankProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        ranks {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Rank {
                        id
                        unitId
                        unit {
                            id
                            areaId
                            code
                            name
                        }
                        code
                        name
                        order

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
query RankProvider($conditions: [ConditionInput]) {
    organization {
        ranks {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation RankProvider($input: RankInput, $operator: String) {
    organization {
        ranks {
            insert(input: $input, operator: $operator) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
mutation RankProvider($inputs: [RankInput], $operator: String) {
    organization {
        ranks {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
mutation RankProvider($input: RankInput, $operator: String) {
    organization {
        ranks {
            update(input: $input, operator: $operator) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
mutation RankProvider($inputs: [RankInput], $operator: String) {
    organization {
        ranks {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                unitId
                unit {
                    id
                    areaId
                    code
                    name
                }
                code
                name
                order

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
mutation RankProvider($ids: [String]) {
    organization {
        ranks {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation RankProvider($conditions: [ConditionInput]) {
    organization {
        ranks {
            deleteBy(conditions: $conditions)
        }
    }
}
```