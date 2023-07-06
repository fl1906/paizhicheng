# 任务拆解

## 任务1 活动创建的增删改查

### 新增活动 （未完成）

![image-20230701160304661](https://img.flya.top/img/image-20230701160304661.png)



创建活动时 入参字段 见apifox 文档：

```json
{
    "address": "河北省海南藏族自治州-",
    "regionId": 76,
    "title": "可需值场列",
    "startTime": "1993-03-17 15:39:25",
    "endDate": "1978-09-02",
    "saleEndTime": "1991-09-25 05:21:52",
    "showTime": "1971-04-04 04:37:50",
    "coverImage": "http://dummyimage.com/400x400",
    "introList": [
        {
            "introId": 1,
            "content": "incididunt culpa dolore labore",
            "imageFullUrl": "http://dummyimage.com/400x400"
        }
    ],
    "tagList": [
        {
            "tagId": "1",
            "name": "治四八型变院",
            "imageUrl": "http://dummyimage.com/400x400"
        }
    ],
    "artistList": [
        {
            "name": "深选布市治",
            "description": "保办场知养力气现由合该取。引化员些务林酸你百市积时华界深。结飞同细运东满至说年它式家江始。",
            "imageUrl": "http://dummyimage.com/400x400",
            "artistId": 1
        }
    ],
    "organizerList": {
        "content": "in",
        "logo": "id minim voluptate nulla",
        "name": "合名东正般办",
        "phone": "18673576726",
        "organizerId": 1
    },
    "activityId": 45, /** 这个在创建的时候不传 */
    "classify": 0 , /** 0电音节 1 派对 目前就这两个分类 **/
    "region": 0  /** 0国内 1国外 当且仅当电音节时需要这个选项 **/
}
```



![image-20230701161151487](https://img.flya.top/img/image-20230701161151487.png)

需要注意的是 创建活动时 可以 在此 创建活动简介 （或者选取多 个活动简介) 标签和艺人同理  主办方只能选一个 或者创建一个



### 修改活动 （未完成）

修改活动同创建活动一致  可以修改活动信息 并创建活动简介 （或者选取多 个活动简介) 标签和艺人同理  主办方只能选一个 或者创建一个 入参多一个活动Id

```json
{
    "address": "河北省海南藏族自治州-",
    "regionId": 76,
    "title": "可需值场列",
    "startTime": "1993-03-17 15:39:25",
    "endDate": "1978-09-02",
    "saleEndTime": "1991-09-25 05:21:52",
    "showTime": "1971-04-04 04:37:50",
    "coverImage": "http://dummyimage.com/400x400",
    "introList": [
        {
            "introId": 1,
            "content": "incididunt culpa dolore labore",
            "imageFullUrl": "http://dummyimage.com/400x400"
        }
    ],
    "tagList": [
        {
            "tagId": "1",
            "name": "治四八型变院",
            "imageUrl": "http://dummyimage.com/400x400"
        }
    ],
    "artistList": [
        {
            "name": "深选布市治",
            "description": "保办场知养力气现由合该取。引化员些务林酸你百市积时华界深。结飞同细运东满至说年它式家江始。",
            "imageUrl": "http://dummyimage.com/400x400",
            "artistId": 1
        }
    ],
    "organizerList": {
        "content": "in",
        "logo": "id minim voluptate nulla",
        "name": "合名东正般办",
        "phone": "18673576726",
        "organizerId": 1
    },
    "activityId": 45, /** 这个在创建的时候不传 */
    "classify": 0 , /** 0电音节 1 派对 目前就这两个分类 **/
    "region": 0  /** 0国内 1国外 当且仅当电音节时需要这个选项 **/
}
```



### 删除活动 （未完成）

删除活动逻辑 无特殊操作 详情见 接口



### 活动列表 与小程序展示活动列表  （未完成）



后台管理活动列表接口 见api fox



```json
{
    "total": 2,
    "rows": [
        {
            "activityId": 47,
            "address": "河北省海南藏族自治州-",
            "regionId": 76,
            "title": "可需值场列",
            "startTime": "1993-03-17 15:39:25",
            "endDate": "1978-09-02",
            "saleEndTime": 1991,
            "showTime": "1971-04-04 04:37:50",
            "coverImage": "http://dummyimage.com/400x400",
            "createTime": "2023-07-02 13:13:14",
            "updateTime": "2023-07-02 13:13:14",
            "state": 1,
            "organizerId": 1,
            "classify": 0,
            "region": 0,
            "introList": [
                {
                    "createTime": "2023-06-29 17:42:22",
                    "updateTime": "2023-06-29 17:42:22",
                    "introId": 1,
                    "content": "我是介绍",
                    "imageFullUrl": "1674352590650974209",
                    "state": 1
                }
            ],
            "tagList": [
                {
                    "createTime": "2023-06-03 16:26:01",
                    "updateTime": "2023-06-03 16:26:01",
                    "tagId": 1,
                    "name": "流行音乐",
                    "imageUrl": "1664911296929222658",
                    "state": 1
                }
            ],
            "artistList": [
                {
                    "createTime": "2023-06-01 23:44:59",
                    "updateTime": "2023-06-01 23:44:59",
                    "artistId": 1,
                    "name": "蔡徐坤",
                    "imageUrl": "1664296307986059265",
                    "description": "坤坤",
                    "state": 1
                }
            ],
            "organizerList": {
                "createTime": "2023-06-03 15:35:47",
                "updateTime": "2023-06-03 15:35:47",
                "organizerId": 1,
                "phone": "111",
                "name": "111",
                "logo": "1664898651010383874",
                "content": "1111",
                "state": 1
            }
        },
        {
            "activityId": 48,
            "address": "河北省海南藏族自治州-",
            "regionId": 76,
            "title": "可需值场列",
            "startTime": "1993-03-17 15:39:25",
            "endDate": "1978-09-02",
            "saleEndTime": 1991,
            "showTime": "1971-04-04 04:37:50",
            "coverImage": "http://dummyimage.com/400x400",
            "createTime": "2023-07-02 13:15:41",
            "updateTime": "2023-07-02 13:15:41",
            "state": 1,
            "organizerId": 1,
            "classify": 0,
            "region": 0,
            "introList": [
                {
                    "createTime": "2023-06-29 17:42:22",
                    "updateTime": "2023-06-29 17:42:22",
                    "introId": 1,
                    "content": "我是介绍",
                    "imageFullUrl": "1674352590650974209",
                    "state": 1
                }
            ],
            "tagList": [
                {
                    "createTime": "2023-06-03 16:26:01",
                    "updateTime": "2023-06-03 16:26:01",
                    "tagId": 1,
                    "name": "流行音乐",
                    "imageUrl": "1664911296929222658",
                    "state": 1
                }
            ],
            "artistList": [
                {
                    "createTime": "2023-06-01 23:44:59",
                    "updateTime": "2023-06-01 23:44:59",
                    "artistId": 1,
                    "name": "蔡徐坤",
                    "imageUrl": "1664296307986059265",
                    "description": "坤坤",
                    "state": 1
                }
            ],
            "organizerList": {
                "createTime": "2023-06-03 15:35:47",
                "updateTime": "2023-06-03 15:35:47",
                "organizerId": 1,
                "phone": "111",
                "name": "111",
                "logo": "1664898651010383874",
                "content": "1111",
                "state": 1
            }
        }
    ],
    "code": 200,
    "msg": "查询成功"
}
```





小程序端列表接口 见apifox







<hr>





## 任务2 用户的增删改查

### 后台界面样式调整 （未完成）

![image-20230704015124282](https://img.flya.top/img/image-20230704015124282.png)









### 用户登录(未完成)

首先获取Code 

https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html

用户基本信息

https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html



https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/userProfile.html

获取用户手机号

https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/getPhoneNumber.html

https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-info/phone-number/getPhoneNumber.html



然后调用登录接口 未注册用户会自动生成用户信息

![image-20230706134652282](https://img.flya.top/img/image-20230706134652282.png)



### 用户信息CRUD （未完成）

获取用户信息

携带上一步登录获取到的Token  调用用户信息接口

![image-20230706141709459](https://img.flya.top/img/image-20230706141709459.png)



修改用户信息（用户昵称只能每一年只能修改一次 其他无限制）







### 用户充值 （未完成）





### 用户收藏的活动列表CRUD （未完成）







# 问题记录

## 自增系数过大

### 修改初始自增系数

`ALTER TABLE your_table AUTO_INCREMENT = 1;` 是一条SQL语句，用于修改表中自增主键的起始值。

当您向表中插入新记录时，如果该表具有自增主键，MySQL将自动为每个插入的行分配一个唯一的自增值。自增值的起始点可以通过修改自增计数器的值进行调整。

在上述代码中，`your_table`是要进行更改的表的名称。`AUTO_INCREMENT`是自增列的属性，`1`是将自增列的起始值设置为1。这意味着下一次插入记录时，自增列将从1开始递增。



### 设置为自增主键

### ![image-20230706141523916](https://img.flya.top/img/image-20230706141523916.png)

















