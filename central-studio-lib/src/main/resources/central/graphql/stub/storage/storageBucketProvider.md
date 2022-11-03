findById
===

* 根据主键查询数据

```graphql
query StorageBucketProvider($id: String) {
    storage {
        buckets {
            findById(id: $id) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
query StorageBucketProvider($ids: [String]) {
    storage {
        buckets {
            findByIds(ids: $ids){
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
query StorageBucketProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        buckets {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
query StorageBucketProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    storage {
        buckets {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on StorageBucket {
                        id
                        applicationId
                        application {
                            id
                            code
                            name
                            secret
                            enabled
                        }
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
query StorageBucketProvider($conditions: [ConditionInput]) {
    storage {
        buckets {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation StorageBucketProvider($input: StorageBucketInput, $operator: String) {
    storage {
        buckets {
            insert(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
mutation StorageBucketProvider($inputs: [StorageBucketInput], $operator: String) {
    storage {
        buckets {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
mutation StorageBucketProvider($input: StorageBucketInput, $operator: String) {
    storage {
        buckets {
            update(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
mutation StorageBucketProvider($inputs: [StorageBucketInput], $operator: String) {
    storage {
        buckets {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                    secret
                    enabled
                }
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
mutation StorageBucketProvider($ids: [String]) {
    storage {
        buckets {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation StorageBucketProvider($conditions: [ConditionInput]) {
    storage {
        buckets {
            deleteBy(conditions: $conditions)
        }
    }
}
```