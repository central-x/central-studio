findById
===

* 根据主键查询数据

```graphql
query ApplicationProvider($id: String) {
    ten {
        applications {
            findById(id: $id) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
query ApplicationProvider($ids: [String]) {
    ten {
        applications {
            findByIds(ids: $ids){
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
query ApplicationProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    ten {
        applications {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
query ApplicationProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    ten {
        applications {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Application {
                        id
                        code
                        name
                        logo
                        url
                        contextPath
                        key
                        enabled
                        remark
                        modules {
                            id
                            applicationId
                            url
                            contextPath
                            enabled
                            remark
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
query ApplicationProvider($conditions: [ConditionInput]) {
    ten {
        applications {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation ApplicationProvider($input: ApplicationInput, $operator: String) {
    ten {
        applications {
            insert(input: $input, operator: $operator) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
mutation ApplicationProvider($inputs: [ApplicationInput], $operator: String) {
    ten {
        applications {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
mutation ApplicationProvider($input: ApplicationInput, $operator: String) {
    ten {
        applications {
            update(input: $input, operator: $operator) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
mutation ApplicationProvider($inputs: [ApplicationInput], $operator: String) {
    ten {
        applications {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark
                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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
mutation ApplicationProvider($ids: [String]) {
    ten {
        applications {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation ApplicationProvider($conditions: [ConditionInput]) {
    ten {
        applications {
            deleteBy(conditions: $conditions)
        }
    }
}
```