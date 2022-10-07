findById
===

* 根据主键查询数据

```graphql
query AreaProvider($id: String) {
    org {
        areas {
            findById(id: $id) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
query AreaProvider($ids: [String]) {
    org {
        areas {
            findByIds(ids: $ids){
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
query AreaProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    org {
        areas {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
query AreaProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    org {
        areas {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Area {
                        id
                        parentId
                        parent {
                            id
                            parentId
                            code
                            name
                            type
                            order
                        }
                        code
                        name
                        type
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
query AreaProvider($conditions: [ConditionInput]) {
    org {
        areas {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation AreaProvider($input: AreaInput, $operator: String) {
    org {
        areas {
            insert(input: $input, operator: $operator) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
mutation AreaProvider($inputs: [AreaInput], $operator: String) {
    org {
        areas {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
mutation AreaProvider($input: AreaInput, $operator: String) {
    org {
        areas {
            update(input: $input, operator: $operator) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
mutation AreaProvider($inputs: [AreaInput], $operator: String) {
    org {
        areas {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                parentId
                parent {
                    id
                    parentId
                    code
                    name
                    type
                    order
                }
                code
                name
                type
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
mutation AreaProvider($ids: [String]) {
    org {
        areas {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation AreaProvider($conditions: [ConditionInput]) {
    org {
        areas {
            deleteBy(conditions: $conditions)
        }
    }
}
```