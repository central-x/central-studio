findById
===

* 根据主键查询数据

```graphql
query MulticastMessageProvider($id: String) {
    multicast {
        messages {
            findById(id: $id) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
query MulticastMessageProvider($ids: [String]) {
    multicast {
        messages {
            findByIds(ids: $ids){
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
query MulticastMessageProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    multicast {
        messages {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
query MulticastMessageProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    multicast {
        messages {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on MulticastMessage {
                        id
                        broadcasterId
                        broadcaster {
                            id
                            code
                            name
                            enabled
                        }
                        body
                        mode
                        status

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
query MulticastMessageProvider($conditions: [ConditionInput]) {
    multicast {
        messages {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation MulticastMessageProvider($input: MulticastMessageInput, $operator: String) {
    multicast {
        messages {
            insert(input: $input, operator: $operator) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
mutation MulticastMessageProvider($inputs: [MulticastMessageInput], $operator: String) {
    multicast {
        messages {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
mutation MulticastMessageProvider($input: MulticastMessageInput, $operator: String) {
    multicast {
        messages {
            update(input: $input, operator: $operator) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
mutation MulticastMessageProvider($inputs: [MulticastMessageInput], $operator: String) {
    multicast {
        messages {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                broadcasterId
                broadcaster {
                    id
                    code
                    name
                    enabled
                }
                body
                mode
                status

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
mutation MulticastMessageProvider($ids: [String]) {
    multicast {
        messages {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation MulticastMessageProvider($conditions: [ConditionInput]) {
    multicast {
        messages {
            deleteBy(conditions: $conditions)
        }
    }
}
```