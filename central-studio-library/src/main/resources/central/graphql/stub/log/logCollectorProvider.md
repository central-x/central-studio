findById
===

* 根据主键查询数据

```graphql
query LogCollectorProvider($id: String) {
    log {
        collectors {
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
query LogCollectorProvider($ids: [String]) {
    log {
        collectors {
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
query LogCollectorProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        collectors {
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
query LogCollectorProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        collectors {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on LogCollector {
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
query LogCollectorProvider($conditions: [ConditionInput]) {
    log {
        collectors {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation LogCollectorProvider($input: LogCollectorInput, $operator: String) {
    log {
        collectors {
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
mutation LogCollectorProvider($inputs: [LogCollectorInput], $operator: String) {
    log {
        collectors {
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
mutation LogCollectorProvider($input: LogCollectorInput, $operator: String) {
    log {
        collectors {
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
mutation LogCollectorProvider($inputs: [LogCollectorInput], $operator: String) {
    log {
        collectors {
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
mutation LogCollectorProvider($ids: [String]) {
    log {
        collectors {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation LogCollectorProvider($conditions: [ConditionInput]) {
    log {
        collectors {
            deleteBy(conditions: $conditions)
        }
    }
}
```