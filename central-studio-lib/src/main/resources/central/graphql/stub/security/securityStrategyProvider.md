findById
===

* 根据主键查询数据

```graphql
query SecurityStrategyProvider($id: String) {
    security {
        strategies {
            findById(id: $id) {
                id
                code
                name
                type
                enabled
                remark
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
query SecurityStrategyProvider($ids: [String]) {
    security {
        strategies {
            findByIds(ids: $ids){
                id
                code
                name
                type
                enabled
                remark
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
query SecurityStrategyProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    security {
        strategies {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                code
                name
                type
                enabled
                remark
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
query SecurityStrategyProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    security {
        strategies {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on SecurityStrategy {
                        id
                        code
                        name
                        type
                        enabled
                        remark
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
query SecurityStrategyProvider($conditions: [ConditionInput]) {
    security {
        strategies {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation SecurityStrategyProvider($input: SecurityStrategyInput, $operator: String) {
    security {
        strategies {
            insert(input: $input, operator: $operator) {
                id
                code
                name
                type
                enabled
                remark
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
mutation SecurityStrategyProvider($inputs: [SecurityStrategyInput], $operator: String) {
    security {
        strategies {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                type
                enabled
                remark
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
mutation SecurityStrategyProvider($input: SecurityStrategyInput, $operator: String) {
    security {
        strategies {
            update(input: $input, operator: $operator) {
                id
                code
                name
                type
                enabled
                remark
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
mutation SecurityStrategyProvider($inputs: [SecurityStrategyInput], $operator: String) {
    security {
        strategies {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                type
                enabled
                remark
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
mutation SecurityStrategyProvider($ids: [String]) {
    security {
        strategies {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation SecurityStrategyProvider($conditions: [ConditionInput]) {
    security {
        strategies {
            deleteBy(conditions: $conditions)
        }
    }
}
```