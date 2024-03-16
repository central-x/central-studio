findById
===

* 根据主键查询数据

```graphql
query StorageObjectProvider($id: String) {
    storage {
        objects {
            findById(id: $id) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
query StorageObjectProvider($ids: [String]) {
    storage {
        objects {
            findByIds(ids: $ids){
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
query StorageObjectProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        objects {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
query StorageObjectProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        objects {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on StorageObject {
                        id
                        bucketId
                        bucket {
                            id
                            code
                            name
                        }
                        name
                        extension
                        size
                        digest
                        key
                        confirmed

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
query StorageObjectProvider($conditions: [ConditionInput]) {
    storage {
        objects {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation StorageObjectProvider($input: StorageObjectInput, $operator: String) {
    storage {
        objects {
            insert(input: $input, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
mutation StorageObjectProvider($inputs: [StorageObjectInput], $operator: String) {
    storage {
        objects {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
mutation StorageObjectProvider($input: StorageObjectInput, $operator: String) {
    storage {
        objects {
            update(input: $input, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
mutation StorageObjectProvider($inputs: [StorageObjectInput], $operator: String) {
    storage {
        objects {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                name
                extension
                size
                digest
                key
                confirmed

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
mutation StorageObjectProvider($ids: [String]) {
    storage {
        objects {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation StorageObjectProvider($conditions: [ConditionInput]) {
    storage {
        objects {
            deleteBy(conditions: $conditions)
        }
    }
}
```