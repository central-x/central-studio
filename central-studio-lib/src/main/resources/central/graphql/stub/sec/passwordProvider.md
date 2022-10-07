findById
===

* 根据主键查询数据

```graphql
query PasswordProvider($id: String) {
    sec {
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
    sec {
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
    sec {
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
    sec {
        passwords {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Password {
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
    sec {
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
mutation PasswordProvider($input: PasswordInput, $operator: String) {
    sec {
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
mutation PasswordProvider($inputs: [PasswordInput], $operator: String) {
    sec {
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
    sec {
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
    sec {
        passwords {
            deleteBy(conditions: $conditions)
        }
    }
}
```