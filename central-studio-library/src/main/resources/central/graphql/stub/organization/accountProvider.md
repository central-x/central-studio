findById
===

* 根据主键查询数据

```graphql
query AccountProvider($id: String) {
    organization {
        accounts {
            findById(id: $id) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
query AccountProvider($ids: [String]) {
    organization {
        accounts {
            findByIds(ids: $ids){
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
query AccountProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
query AccountProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Account {
                        id
                        username
                        email
                        mobile
                        name
                        avatar
                        admin
                        supervisor
                        enabled
                        deleted

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
query AccountProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation AccountProvider($input: AccountInput, $operator: String) {
    organization {
        accounts {
            insert(input: $input, operator: $operator) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
mutation AccountProvider($inputs: [AccountInput], $operator: String) {
    organization {
        accounts {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
mutation AccountProvider($input: AccountInput, $operator: String) {
    organization {
        accounts {
            update(input: $input, operator: $operator) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
mutation AccountProvider($inputs: [AccountInput], $operator: String) {
    organization {
        accounts {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                username
                email
                mobile
                name
                avatar
                admin
                supervisor
                enabled
                deleted

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
mutation AccountProvider($ids: [String]) {
    organization {
        accounts {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation AccountProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            deleteBy(conditions: $conditions)
        }
    }
}
```