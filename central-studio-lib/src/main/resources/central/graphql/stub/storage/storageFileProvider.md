findById
===

* 根据主键查询数据

```graphql
query StorageFileProvider($id: String) {
    storage {
        files {
            findById(id: $id) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
query StorageFileProvider($ids: [String]) {
    storage {
        files {
            findByIds(ids: $ids){
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
query StorageFileProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        files {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
query StorageFileProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        files {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on StorageFile {
                        id
                        bucketId
                        bucket {
                            id
                            code
                            name
                        }
                        parentId
                        parent {
                            id
                            code
                            name
                            directory
                        }
                        code
                        name
                        extension
                        directory
                        confirmed
                        children {
                            id
                            code
                            name
                            directory
                        }

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
query StorageFileProvider($conditions: [ConditionInput]) {
    storage {
        files {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation StorageFileProvider($input: StorageFileInput, $operator: String) {
    storage {
        files {
            insert(input: $input, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
mutation StorageFileProvider($inputs: [StorageFileInput], $operator: String) {
    storage {
        files {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
mutation StorageFileProvider($input: StorageFileInput, $operator: String) {
    storage {
        files {
            update(input: $input, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
mutation StorageFileProvider($inputs: [StorageFileInput], $operator: String) {
    storage {
        files {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                bucketId
                bucket {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    code
                    name
                    directory
                }
                code
                name
                extension
                directory
                confirmed
                children {
                    id
                    code
                    name
                    directory
                }

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
mutation StorageFileProvider($ids: [String]) {
    storage {
        files {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation StorageFileProvider($conditions: [ConditionInput]) {
    storage {
        files {
            deleteBy(conditions: $conditions)
        }
    }
}
```