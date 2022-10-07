findById
===

* 查询数据

```graphql
query MenuProvider($id: String) {
    result: Menu_findById(id: $id) {
        id
        code
        name
        icon
        type
        enabled
        order
        remark
        parentId
        children {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        permissions {
            id
            menuId
            code
            name
            enabled
            remark

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
```

findPermissionById
===

* 查询权限数据

```graphql
query MenuProvider($id: String) {
    result: Menu_findPermissionById(id: $id) {
        id
        menuId
        menu {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        code
        name
        enabled
        remark

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
```

findByIds
===

* 查询数据

```graphql
query MenuProvider($ids: [String]) {
    result: Menu_findByIds(ids: $ids) {
        id
        code
        name
        icon
        type
        enabled
        order
        remark
        parentId
        children {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        permissions {
            id
            menuId
            code
            name
            enabled
            remark

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
```

findPermissionByIds
===

* 查询权限数据

```graphql
query MenuProvider($ids: [String]) {
    result: Menu_findPermissionByIds(ids: $ids) {
        id
        menuId
        menu {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        code
        name
        enabled
        remark

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
```

findBy
===

* 查询数据

```graphql
query MenuProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Menu_findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
        id
        code
        name
        icon
        type
        enabled
        order
        remark
        parentId
        children {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        permissions {
            id
            menuId
            code
            name
            enabled
            remark

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
```

findPermissionBy
===

* 查询权限数据

```graphql
query MenuProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Menu_findPermissionBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
        id
        menuId
        menu {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        code
        name
        enabled
        remark

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
```

pageBy
===

* 分页查询数据

```graphql
query MenuProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Menu_pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders) {
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
                children {
                    id
                    code
                    name
                    icon
                    type
                    enabled
                    order
                    remark
                    parentId
                }
                permissions {
                    id
                    menuId
                    code
                    name
                    enabled
                    remark

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

pagePermissionBy
===

* 分页查询权限数据

```graphql
query MenuProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Menu_pagePermissionBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders) {
        pager {
            pageIndex
            pageSize
            pageCount
            itemCount
        }
        data {
            ... on Permission {
                id
                menuId
                menu {
                    id
                    code
                    name
                    icon
                    type
                    enabled
                    order
                    remark
                    parentId
                }
                code
                name
                enabled
                remark

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

countBy
===

* 查询符合条件的数据数量

```graphql
query MenuProvider($conditions: [ConditionInput]) {
    result: Menu_countBy(conditions: $conditions)
}
```

countPermissionBy
===

* 查询符合条件的数据数量

```graphql
query MenuProvider($conditions: [ConditionInput]) {
    result: Menu_countPermissionBy(conditions: $conditions)
}
```

insert
===

* 保存数据

```graphql
mutation MenuProvider($input: MenuInput, $operator: String) {
    result: Menu_insert(input: $input, operator: $operator) {
        id
        code
        name
        icon
        type
        enabled
        order
        remark
        parentId
        children {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        permissions {
            id
            menuId
            code
            name
            enabled
            remark

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
```

insertPermission
===

* 保存权限数据

```graphql
mutation MenuProvider($input: PermissionInput, $operator: String) {
    result: Menu_insertPermission(input: $input, operator: $operator) {
        id
        menuId
        menu {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        code
        name
        enabled
        remark

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
```

update
===

* 更新数据

```graphql
mutation MenuProvider($input: MenuInput, $operator: String) {
    result: Menu_update(input: $input, operator: $operator) {
        id
        code
        name
        icon
        type
        enabled
        order
        remark
        parentId
        children {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        permissions {
            id
            menuId
            code
            name
            enabled
            remark

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
```

updatePermission
===

* 更新数据

```graphql
mutation MenuProvider($input: PermissionInput, $operator: String) {
    result: Menu_updatePermission(input: $input, operator: $operator) {
        id
        menuId
        menu {
            id
            code
            name
            icon
            type
            enabled
            order
            remark
            parentId
        }
        code
        name
        enabled
        remark

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
```

deleteByIds
===

* 删除数据

```graphql
mutation MenuProvider($ids: [String]) {
    result: Menu_deleteByIds(ids: $ids)
}
```

deletePermissionByIds
===

* 删除权限数据

```graphql
mutation MenuProvider($ids: [String]) {
    result: Menu_deletePermissionByIds(ids: $ids)
}
```