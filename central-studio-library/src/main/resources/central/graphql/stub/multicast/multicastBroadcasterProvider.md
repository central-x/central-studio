findById
===

* 根据主键查询数据

```graphql
query MulticastBroadcasterProvider($id: String) {
    multicast {
        broadcasters {
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
query MulticastBroadcasterProvider($ids: [String]) {
    multicast {
        broadcasters {
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
query MulticastBroadcasterProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    multicast {
        broadcasters {
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
query MulticastBroadcasterProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    multicast {
        broadcasters {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on MulticastBroadcaster {
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
query MulticastBroadcasterProvider($conditions: [ConditionInput]) {
    multicast {
        broadcasters {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation MulticastBroadcasterProvider($input: MulticastBroadcasterInput, $operator: String) {
    multicast {
        broadcasters {
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
mutation MulticastBroadcasterProvider($inputs: [MulticastBroadcasterInput], $operator: String) {
    multicast {
        broadcasters {
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
mutation MulticastBroadcasterProvider($input: MulticastBroadcasterInput, $operator: String) {
    multicast {
        broadcasters {
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
mutation MulticastBroadcasterProvider($inputs: [MulticastBroadcasterInput], $operator: String) {
    multicast {
        broadcasters {
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
mutation MulticastBroadcasterProvider($ids: [String]) {
    multicast {
        broadcasters {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation MulticastBroadcasterProvider($conditions: [ConditionInput]) {
    multicast {
        broadcasters {
            deleteBy(conditions: $conditions)
        }
    }
}
```