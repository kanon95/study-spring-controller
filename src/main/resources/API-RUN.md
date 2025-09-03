
### 사용자 등록
```
POST http://localhost:8080/api/users
{
    "name": "새사용자1",
    "email": "new1@example.com",
    "phone": "010-0000-0001"
}
```


### 사용자 수정
```
PUT http://localhost:8080/api/users/1
{
    "name": "새사용자1-update",
    "email": "new1-update@example.com",
    "phone": "010-1111-0001"
}
```

### 사용자 조회
```
GET http://localhost:8080/api/users
```

### 사용자 삭제
```
DELETE http://localhost:8080/api/users/1
```






