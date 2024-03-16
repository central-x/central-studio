findById
===

* 根据主键查询数据

```graphql
query PostProvider($id: String) {
    organization {
        posts {
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
query PostProvider($ids: [String]) {
    organization {
        posts {
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
query PostProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        posts {
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
query PostProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        posts {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Post {
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
query PostProvider($conditions: [ConditionInput]) {
    organization {
        posts {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation PostProvider($input: PostInput, $operator: String) {
    organization {
        posts {
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
mutation PostProvider($inputs: [PostInput], $operator: String) {
    organization {
        posts {
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
mutation PostProvider($input: PostInput, $operator: String) {
    organization {
        posts {
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
mutation PostProvider($inputs: [PostInput], $operator: String) {
    organization {
        posts {
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
mutation PostProvider($ids: [String]) {
    organization {
        posts {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation PostProvider($conditions: [ConditionInput]) {
    organization {
        posts {
            deleteBy(conditions: $conditions)
        }
    }
}
```