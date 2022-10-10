findById
===

* 根据主键查询数据

```graphql
query MenuProvider($id: String) {
    sec {
        menus {
            findById(id: $id) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
query MenuProvider($ids: [String]) {
    sec {
        menus {
            findByIds(ids: $ids){
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
query MenuProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    sec {
        menus {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
query MenuProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    sec {
        menus {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Menu {
                        id
                        code
                        name
                        icon
                        type
                        enabled
                        order
                        remark
                        parentId
                        parent {
                            id
                            code
                            name
                            enabled
                        }
                        children {
                            id
                            code
                            name
                            enabled
                        }
                        permissions {
                            id
                            menuId
                            code
                            name
                            enabled
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
query MenuProvider($conditions: [ConditionInput]) {
    sec {
        menus {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation MenuProvider($input: MenuInput, $operator: String) {
    sec {
        menus {
            insert(input: $input, operator: $operator) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
mutation MenuProvider($inputs: [MenuInput], $operator: String) {
    sec {
        menus {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
mutation MenuProvider($input: MenuInput, $operator: String) {
    sec {
        menus {
            update(input: $input, operator: $operator) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
mutation MenuProvider($inputs: [MenuInput], $operator: String) {
    sec {
        menus {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                icon
                type
                enabled
                order
                remark
                parentId
                parent {
                    id
                    code
                    name
                    enabled
                }
                children {
                    id
                    code
                    name
                    enabled
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
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
mutation MenuProvider($ids: [String]) {
    sec {
        menus {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation MenuProvider($conditions: [ConditionInput]) {
    sec {
        menus {
            deleteBy(conditions: $conditions)
        }
    }
}
```