findById
===

* 根据主键查询数据

```graphql
query IdentityRecordProvider($id: String) {
    identity {
        records {
            findById(id: $id) {
                id
                address
                host
                device

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
query IdentityRecordProvider($ids: [String]) {
    identity {
        records {
            findByIds(ids: $ids){
                id
                address
                host
                device

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
query IdentityRecordProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    identity {
        records {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                address
                host
                device

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
query IdentityRecordProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    identity {
        records {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on IdentityRecord {
                        id
                        address
                        host
                        device

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
query IdentityRecordProvider($conditions: [ConditionInput]) {
    identity {
        records {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation IdentityRecordProvider($input: IdentityRecordInput, $operator: String) {
    identity {
        records {
            insert(input: $input, operator: $operator) {
                id
                address
                host
                device

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
mutation IdentityRecordProvider($inputs: [IdentityRecordInput], $operator: String) {
    identity {
        records {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                address
                host
                device

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
mutation IdentityRecordProvider($input: IdentityRecordInput, $operator: String) {
    identity {
        records {
            update(input: $input, operator: $operator) {
                id
                address
                host
                device

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
mutation IdentityRecordProvider($inputs: [IdentityRecordInput], $operator: String) {
    identity {
        records {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                address
                host
                device

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
mutation IdentityRecordProvider($ids: [String]) {
    identity {
        records {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation IdentityRecordProvider($conditions: [ConditionInput]) {
    identity {
        records {
            deleteBy(conditions: $conditions)
        }
    }
}
```