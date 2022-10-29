findById
===

* 根据主键查询数据

```graphql
query LogStorageProvider($id: String) {
    log {
        storages {
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
query LogStorageProvider($ids: [String]) {
    log {
        storages {
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
query LogStorageProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        storages {
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
query LogStorageProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        storages {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on LogStorage {
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
query LogStorageProvider($conditions: [ConditionInput]) {
    log {
        storages {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation LogStorageProvider($input: LogStorageInput, $operator: String) {
    log {
        storages {
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
mutation LogStorageProvider($inputs: [LogStorageInput], $operator: String) {
    log {
        storages {
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
mutation LogStorageProvider($input: LogStorageInput, $operator: String) {
    log {
        storages {
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
mutation LogStorageProvider($inputs: [LogStorageInput], $operator: String) {
    log {
        storages {
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
mutation LogStorageProvider($ids: [String]) {
    log {
        storages {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation LogStorageProvider($conditions: [ConditionInput]) {
    log {
        storages {
            deleteBy(conditions: $conditions)
        }
    }
}
```