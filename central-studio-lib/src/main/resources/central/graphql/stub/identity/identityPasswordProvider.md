findById
===

* 根据主键查询数据

```graphql
query PasswordProvider($id: String) {
    identity {
        passwords {
            findById(id: $id) {
                id
                accountId
                account {
                    id
                    username
                    name
                }
                value

                creatorId
                createDate
                creator {
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
query PasswordProvider($ids: [String]) {
    identity {
        passwords {
            findByIds(ids: $ids){
                id
                accountId
                account {
                    id
                    username
                    name
                }
                value

                creatorId
                createDate
                creator {
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
query PasswordProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    identity {
        passwords {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                accountId
                account {
                    id
                    username
                    name
                }
                value

                creatorId
                createDate
                creator {
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
query PasswordProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    identity {
        passwords {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on SecurityPassword {
                        id
                        accountId
                        account {
                            id
                            username
                            name
                        }
                        value

                        creatorId
                        createDate
                        creator {
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
query PasswordProvider($conditions: [ConditionInput]) {
    identity {
        passwords {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation PasswordProvider($input: SecurityPasswordInput, $operator: String) {
    identity {
        passwords {
            insert(input: $input, operator: $operator) {
                id
                accountId
                account {
                    id
                    username
                    name
                }
                value

                creatorId
                createDate
                creator {
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
mutation PasswordProvider($inputs: [SecurityPasswordInput], $operator: String) {
    identity {
        passwords {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                accountId
                account {
                    id
                    username
                    name
                }
                value

                creatorId
                createDate
                creator {
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
mutation PasswordProvider($ids: [String]) {
    identity {
        passwords {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation PasswordProvider($conditions: [ConditionInput]) {
    identity {
        passwords {
            deleteBy(conditions: $conditions)
        }
    }
}
```