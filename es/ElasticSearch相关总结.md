# ElasticSearch相关总结

## 一、 相关安装

### 1.1 安装es

```shell
docker network create esnet
```

```shell
docker pull --platform linux/x86_64 elasticsearch:7.16.1
```

修改elasticsearch.yml

```yml
http.host: 0.0.0.0
```

```shell
docker run --name elasticsearch -p 9200:9200  -p 9300:9300 -e "discovery.type=single-node" \
--net esnet \
-e ES_JAVA_OPTS="-Xms84m -Xmx512m" \
-v /Users/dexlace/develop-software/es/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml  \
-v /Users/dexlace/develop-software/es/data:/usr/share/elasticsearch/data  \
-v /Users/dexlace/develop-software/es/plugins:/usr/share/elasticsearch/plugins -d elasticsearch:7.16.1
```

访问`localhost:9200`

把对应版本的ik分词器下载解压到`/Users/dexlace/develop-software/es/plugins`

<img src="/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20221101014412979.png" alt="image-20221101014412979" style="zoom:50%;" />

### 1.2 安装es head

安装es head

```shell
# cat elasticsearch.yml
http.host: 0.0.0.0
http.cors.enabled: true
http.cors.allow-origin: "*"
```

```shell
docker pull --platform linux/x86_64 mobz/elasticsearch-head:5
```

```shell
docker run -d --name es-head --net esnet -p 9100:9100 mobz/elasticsearch-head:5
```

### 1.3 安装kibana

 安装kibana

https://hub.docker.com/_/kibana

```shell
docker pull --platform linux/x86_64 kibana:7.16.1
```

```shell
docker run --name kibana --net esnet -e ELASTICSEARCH_HOSTS=http://172.20.0.2:9200 -p 5601:5601 \
-d kibana:7.16.1
```

注意`ELASTICSEARCH_HOSTS`必须是es暴露出来的ip,使用`docker inspect es的容器id`

<img src="/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20221101111443801.png" alt="image-20221101111443801" style="zoom:50%;" />

或者使用配置文件启动

```yml
server.name: kibana
server.host: "0"
elasticsearch.hosts: [ "http://172.20.0.2:9200" ]

```

```shell
docker run --name kibana --net esnet \
-v /Users/dexlace/develop-software/kibana/config/kibana.yml:/usr/share/kibana/config/kibana.yml \
-p 5601:5601 -d kibana:7.16.1
```

## 二、索引、映射、文档

索引---对应于数据库

映射---对应于表结构

文档---对应于行数据

### 2.1 索引

**`一个索引就是一个拥有几分相似特征的文档的集合`**。比如说，你可以有一个商品数据的索引，一个订单数据的索引，还有一个用户数据的索引。**`一个索引由一个名字来标识`**`(必须全部是小写字母的)`**，**并且当我们要对这个索引中的文档进行索引、搜索、更新和删除的时候，都要使用到这个名字。

#### 创建

```markdown
# 1.创建索引
- PUT /索引名 ====> PUT /products
- 注意: 
		1.ES中索引健康转态  red(索引不可用) 、yellwo(索引可用,存在风险)、green(健康)
		2.默认ES在创建索引时回为索引创建1个备份索引和一个primary索引
		
# 2.创建索引 进行索引分片配置
- PUT /products
{
  "settings": {
    "number_of_shards": 1, #指定主分片的数量
    "number_of_replicas": 0 #指定副本分片的数量
  }
}
```

![image-20211020205824120](/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20211020205824120.png)

#### 查询

```markdown
# 查询索引
- GET /_cat/indices?v
```

![image-20211020210043010](/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20211020210043010.png)

#### 删除

```markdown
# 3.删除索引
- DELETE /索引名 =====> DELETE /products
- DELETE /*     `*代表通配符,代表所有索引`
```

![image-20211020205934645](/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20211020205934645.png)

### 2.2 映射

**`映射是定义一个文档和它所包含的字段如何被存储和索引的过程`**。在默认配置下，ES可以根据插入的数据`自动地创建mapping，也可以手动创建mapping`。 mapping中主要包括字段名、字段类型等 

映射在创建索引时默认就会创建

不能单独创建索引

==必须创建索引时创建映射==

#### 2.2.1 创建

```markdown
# 创建索引并指定映射
PUT /products
{ 
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  }, 
  "mappings": {
    "properties": {
      "title":{
        "type": "keyword"
      },
      "price":{
        "type": "double"
      },
      "created_at":{
        "type": "date"
      },
      "description":{
        "type": "text"
      }
    }
  }
 }
```

#### 2.2.2 查询

```markdown
GET /索引名/_mapping 
```

### 2.3 文档

#### 2.3.1 创建文档

`指定文档id`,指定文档id为1

```http

POST /products/_doc/1 
{
  "id":1,
  "title":"iphone13",
  "price":8999.99,
  "created_at":"2021-09-15",
  "description":"iPhone 13屏幕采用6.1英寸OLED屏幕。"
}

```

`自动生成文档id`

```http

POST /products/_doc/ 
{
  "title":"iphone14",
  "price":8999.99,
  "created_at":"2021-09-15",
  "description":"iPhone 13屏幕采用6.8英寸OLED屏幕"
}
```

<img src="/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20221101210052463.png" alt="image-20221101210052463" style="zoom:50%;" />

#### 2.3.2 文档查询

```json
GET /products/_doc/1
```

#### 2.3.3 删除文档

```json
DELETE /products/_doc/1
```

#### 2.3.4 更新文档

```json
PUT /products/_doc/1
{
  "title":"iphon15"
}

// 会删除原始文档 并从新添加
```

```json
POST /products/_doc/1/_update
{
    "doc" : {
        "title" : "iphon15"
    }
}
// 会更改指定字段
```

#### 2.3.5 批量操作

```http
POST /products/_doc/_bulk #批量索引两条文档
 	{"index":{"_id":"1"}}
  		{"title":"iphone14","price":8999.99,"created_at":"2021-09-15","description":"iPhone 13屏幕采用6.8英寸OLED屏幕"}
	{"index":{"_id":"2"}}
  		{"title":"iphone15","price":8999.99,"created_at":"2021-09-15","description":"iPhone 15屏幕采用10.8英寸OLED屏幕"}
```

```http
POST /products/_doc/_bulk #更新文档同时删除文档
	{"update":{"_id":"1"}}
		{"doc":{"title":"iphone17"}}
	{"delete":{"_id":2}}
	{"index":{}}
		{"title":"iphone19","price":8999.99,"created_at":"2021-09-15","description":"iPhone 19屏幕采用61.8英寸OLED屏幕"}
```

> 说明:批量时不会因为一个失败而全部失败,而是继续执行后续操作,在返回时按照执行的状态返回!

### 2.4 高级查询语法

​	ES中提供了一种强大的检索数据方式,这种检索方式称之为`Query DSL`<Domain Specified Language> ,`Query DSL`是利用`Rest API传递JSON格式的请求体(Request Body)数据`与ES进行交互，这种方式的`丰富查询语法`让ES检索变得`更强大，更简洁`。

```markdown
# GET /索引名/_doc/_search {json格式请求体数据}
# GET /索引名/_search {json格式请求体数据}
```

- 测试数据

```markdown
# 1.创建索引 映射
PUT /products/
{
  "mappings": {
    "properties": {
      "title":{
        "type": "keyword"
      },
      "price":{
        "type": "double"
      },
      "created_at":{
        "type":"date"
      },
      "description":{
        "type":"text"
      }
    }
  }
}
# 2.测试数据
PUT /products/_doc/_bulk
{"index":{}}
  {"title":"iphone12 pro","price":8999,"created_at":"2020-10-23","description":"iPhone 12 Pro采用超瓷晶面板和亚光质感玻璃背板，搭配不锈钢边框，有银色、石墨色、金色、海蓝色四种颜色。宽度:71.5毫米，高度:146.7毫米，厚度:7.4毫米，重量：187克"}
{"index":{}}
  {"title":"iphone12","price":4999,"created_at":"2020-10-23","description":"iPhone 12 高度：146.7毫米；宽度：71.5毫米；厚度：7.4毫米；重量：162克（5.73盎司） [5]  。iPhone 12设计采用了离子玻璃，以及7000系列铝金属外壳。"}
{"index":{}}
  {"title":"iphone13","price":6000,"created_at":"2021-09-15","description":"iPhone 13屏幕采用6.1英寸OLED屏幕；高度约146.7毫米，宽度约71.5毫米，厚度约7.65毫米，重量约173克。"}
{"index":{}}
  {"title":"iphone13 pro","price":8999,"created_at":"2021-09-15","description":"iPhone 13Pro搭载A15 Bionic芯片，拥有四种配色，支持5G。有128G、256G、512G、1T可选，售价为999美元起。"}
```

#### 常见检索

#### 查询所有[match_all]

> **match_all关键字:**  返回索引中的全部文档

```http
GET /products/_search
{
  "query": {
    "match_all": {}
  }
}
```

#### 关键词查询(term)

>  **term 关键字**: 用来使用关键词查询

```http
GET /products/_search
{
 "query": {
   "term": {
     "price": {
       "value": 4999
     }
   }
 }
}
```

> NOTE1:  通过使用term查询得知ES中默认使用分词器为`标准分词器(StandardAnalyzer),标准分词器对于英文单词分词,对于中文单字分词`。

> NOTE2:  通过使用term查询得知,`在ES的Mapping Type 中 keyword , date ,integer, long , double , boolean or ip 这些类型不分词，只有text类型分词`。

#### 范围查询[range]

> **range 关键字**: 用来指定查询指定范围内的文档

```http
GET /products/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 1400,
        "lte": 9999
      }
    }
  }
}
```

#### 前缀查询[prefix]

> **prefix 关键字**: 用来检索含有指定前缀的关键词的相关文档

```http
GET /products/_search
{
  "query": {
    "prefix": {
      "title": {
        "value": "ipho"
      }
    }
  }
}
```

#### 通配符查询[wildcard]

> **wildcard 关键字**: 通配符查询     **? 用来匹配一个任意字符  * 用来匹配多个任意字符**

```http
GET /products/_search
{
  "query": {
    "wildcard": {
      "description": {
        "value": "iphon*"
      }
    }
  }
}
```

#### 多id查询[ids]

> **ids 关键字** : 值为数组类型,用来根据一组id获取多个对应的文档

```http
GET /products/_search
{
  "query": {
    "ids": {
      "values": ["verUq3wBOTjuBizqAegi","vurUq3wBOTjuBizqAegk"]
    }
  }
}
```

#### 模糊查询[fuzzy]

> **fuzzy 关键字**: 用来模糊查询含有指定关键字的文档

```http
GET /products/_search
{
  "query": {
    "fuzzy": {
      "description": "iphooone"
    }
  }
}
```

> 注意: `fuzzy 模糊查询  最大模糊错误 必须在0-2之间`
>
> - 搜索关键词长度为 2 不允许存在模糊
>
> - 搜索关键词长度为3-5 允许一次模糊
>
> - 搜索关键词长度大于5 允许最大2模糊

#### 布尔查询[bool]

> **bool 关键字**: 用来组合多个条件实现复杂查询
>
> ​	**must: 相当于&& 同时成立**
>
> ​	**should: 相当于|| 成立一个就行**
>
> ​	**must_not: 相当于!  不能满足任何一个**

```http
GET /products/_search
{
  "query": {
    "bool": {
      "must": [
        {"term": {
          "price": {
            "value": 4999
          }
        }}
      ]
    }
  }
}
```

#### 多字段查询[multi_match]

```http
GET /products/_search
{
  "query": {
    "multi_match": {
      "query": "iphone13 毫",
      "fields": ["title","description"]
    }
  }
}
注意: 字段类型分词,将查询条件分词之后进行查询改字段  如果该字段不分词就会将查询条件作为整体进行查询
```

#### 默认字段分词查询[query_string]

```http
GET /products/_search
{
  "query": {
    "query_string": {
      "default_field": "description",
      "query": "屏幕真的非常不错"
    }
  }
}
注意: 查询字段分词就将查询条件分词查询  查询字段不分词将查询条件不分词查询
```

#### 高亮查询[highlight]

> **highlight 关键字**: 可以让符合条件的文档中的关键词高亮

```http
GET /products/_search
{
  "query": {
    "term": {
      "description": {
        "value": "iphone"
      }
    }
  },
  "highlight": {
    "fields": {
      "*":{}
    }
  }
}
```

> **自定义高亮html标签**: 可以在highlight中使用`pre_tags`和`post_tags`

```http
GET /products/_search
{
  "query": {
    "term": {
      "description": {
        "value": "iphone"
      }
    }
  },
  "highlight": {
    "post_tags": ["</span>"], 
    "pre_tags": ["<span style='color:red'>"],
    "fields": {
      "*":{}
    }
  }
}
```

> 多字段高亮 使用`require_field_match`开启多个字段高亮

```http
GET /products/_search
{
  "query": {
    "term": {
      "description": {
        "value": "iphone"
      }
    }
  },
  "highlight": {
    "require_field_match": "false",
    "post_tags": ["</span>"], 
    "pre_tags": ["<span style='color:red'>"],
    "fields": {
      "*":{}
    }
  }
}
```

#### 返回指定条数[size]

> **size 关键字**: 指定查询结果中返回指定条数。  **默认返回值10条**

```http
GET /products/_search
{
  "query": {
    "match_all": {}
  },
  "size": 5
}
```

#### 分页查询[form]

> **from 关键字**: 用来指定起始返回位置，和**size关键字连用可实现分页效果**

```http
GET /products/_search
{
  "query": {
    "match_all": {}
  },
  "size": 5,
  "from": 0
}
```

#### 指定字段排序[sort]

```http
GET /products/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "price": {
        "order": "desc"
      }
    }
  ]
}
```

#### 返回指定字段[_source]

> **_source 关键字**: 是一个数组,在数组中用来指定展示那些字段

```http
GET /products/_search
{
  "query": {
    "match_all": {}
  },
  "_source": ["title","description"]
}
```

### 2.5 索引原理

#### 2.5.1 ==倒排索引==

`倒排索引（Inverted Index）`也叫反向索引，有反向索引必有正向索引。通俗地来讲，`正向索引是通过key找value，反向索引则是通过value找key。ES底层在检索时底层使用的就是倒排索引。`

现有索引和映射如下:

```json
{
  "products" : {
    "mappings" : {
      "properties" : {
        "description" : {
          "type" : "text"  // 默认的分词器会对每个汉字进行分词  
        },
        "price" : {
          "type" : "float"
        },
        "title" : {
          "type" : "keyword"  // 不分词
        }
      }
    }
  }
}
```

先录入如下数据，有三个字段title、price、description等

| _id  | title        | price  | description          |
| ---- | ------------ | ------ | -------------------- |
| 1    | 蓝月亮洗衣液 | `19.9` | 蓝月亮洗衣液`很`高效 |
| 2    | iphone13     | `19.9` | `很`不错的手机       |
| 3    | 小浣熊干脆面 | 1.5    | 小浣熊`很`好吃       |

在ES中==除了text类型分词，其他类型不分词==，因此根据不同字段创建索引如下：

- **title字段:**

  | term         | _id(文档id) |
  | ------------ | ----------- |
  | 蓝月亮洗衣液 | 1           |
  | iphone13     | 2           |
  | 小浣熊干脆面 | 3           |

- **price字段**

  | term | _id(文档id) |
  | ---- | ----------- |
  | 19.9 | [1,2]       |
  | 1.5  | 3           |

- **description字段**

  | term | _id                                      | term | _id  | term | _id  |
  | ---- | ---------------------------------------- | ---- | ---- | ---- | ---- |
  | 蓝   | 1                                        | 不   | 2    | 小   | 3    |
  | 月   | 1                                        | 错   | 2    | 浣   | 3    |
  | 亮   | 1                                        | 的   | 2    | 熊   | 3    |
  | 洗   | 1                                        | 手   | 2    | 好   | 3    |
  | 衣   | 1                                        | 机   | 2    | 吃   | 3    |
  | 液   | 1                                        |      |      |      |      |
  | 很   | [1:1:9,2:1:6,3:1:6]  ==// id:次数:长度== |      |      |      |      |
  | 高   | 1                                        |      |      |      |      |
  | 效   | 1                                        |      |      |      |      |

**`注意: Elasticsearch分别为每个字段都建立了一个倒排索引。因此查询时查询字段的term,就能知道文档ID，就能快速找到文档。`**

### 2.6 Analysis 和 Analyzer

`Analysis`： 文本分析是把==全文本转换一系列单词(term/token)的过程，也叫分词(Analyzer)==。**Analysis是通过Analyzer来实现的**。`分词就是将文档通过Analyzer分成一个一个的Term(关键词查询),每一个Term都指向包含这个Term的文档`。

### 2.7 Analyzer 组成

- ​	注意: 在ES中默认使用标准分词器: StandardAnalyzer 特点: ==中文单字分词  单词分词==

  我是中国人 this is good man---->  analyzer----> 我  是  中  国  人   this  is  good man

> 分析器（analyzer）都由三种构件组成的：`character filters` ， `tokenizers` ， `token filters`。

- ==`character filter` 字符过滤器==
  - 在一段文本进行分词之前，先进行==预处理==，比如说最常见的就是，过滤html标签（<span>hello<span> --> hello），& --> and（I&you --> I and you）
- ==`tokenizers` 分词器==
  - 英文分词可以根据空格将单词分开,中文分词比较复杂,可以采用机器学习算法来分词。

- ==`Token filters` Token过滤器==
  - **将切分的单词进行加工**。大小写转换（例将“Quick”转为小写），去掉停用词（例如停用词像“a”、“and”、“the”等等），加入同义词（例如同义词像“jump”和“leap”）。

`注意:`

- 三者顺序:	Character Filters--->Tokenizer--->Token Filter
- 三者个数：Character Filters（0个或多个） + Tokenizer + Token Filters(0个或多个)

### 2.8 ==内置分词器==

- Standard Analyzer - 默认分词器，英文按单词词切分，并小写处理
- Simple Analyzer - 按照单词切分(符号被过滤), 小写处理
- Stop Analyzer - 小写处理，停用词过滤(the,a,is)
- Whitespace Analyzer - 按照空格切分，不转小写
- Keyword Analyzer - 不分词，直接将输入当作输出

### 2.9 ==内置分词器测试==

- ==标准分词器==
  - 特点: 按照单词分词 英文统一转为小写 过滤标点符号  中文单字分词

```http
POST /_analyze
{
  "analyzer": "standard",
  "text": "this is a , good Man 中华人民共和国"
}
```

- ==Simple 分词器==
  - 特点: 英文按照**单词分词** ==英文统一转为小写 去掉符号 中文按照空格进行分词==

```http
POST /_analyze
{
  "analyzer": "simple",
  "text": "this is a , good Man 中华人民共和国"
}
```

- Whitespace 分词器
  - 特点:  中文 英文 **按照空格分词** ==英文不会转为小写  不去掉标点符号==

```http
POST /_analyze
{
  "analyzer": "whitespace",
  "text": "this is a , good Man"
}
```

### 2.10 ==创建索引设置分词==

```json
PUT /索引名
{
  "settings": {},
  "mappings": {
    "properties": {
      "title":{
        "type": "text",
        "analyzer": "standard" //显示指定分词器
      }
    }
  }
}
```

### 2.11 中文分词器

在ES中支持中文分词器非常多 如 **smartCN**、**IK** 等，推荐的就是 `IK分词器`。

#### 安装IK

开源分词器 Ik 的github:https://github.com/medcl/elasticsearch-analysis-ik

- `注意` IK分词器的版本要你安装ES的版本一致
- `注意` Docker 容器运行 ES 安装插件目录为 **/usr/share/elasticsearch/plugins**

#### IK使用

IK有两种颗粒度的拆分：

- `ik_smart`: 会做最粗粒度的拆分

- `ik_max_word`: 会将文本做最细粒度的拆分

```http
POST /_analyze
{
  "analyzer": "ik_smart",
  "text": "中华人民共和国国歌"
}
```

<img src="/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20221101224156115.png" alt="image-20221101224156115" style="zoom: 33%;" />

```http
POST /_analyze
{
  "analyzer": "ik_max_word",
  "text": "中华人民"
}
```

<img src="/Users/dexlace/private-github-repository/learning-notes/es/ElasticSearch相关总结.assets/image-20211024213133835.png" alt="image-20211024213133835" style="zoom:33%;" />

#### ==扩展词、停用词配置==

IK支持自定义`扩展词典`和`停用词典`

- **`扩展词典`**就是有些词并不是关键词,但是也希望被ES用来作为检索的关键词,可以将这些词加入扩展词典。
- **`停用词典`**就是有些词是关键词,但是出于业务场景不想使用这些关键词被检索到，可以将这些词放入停用词典。

定义扩展词典和停用词典可以修改IK分词器中`config`目录中`IKAnalyzer.cfg.xml`这个文件。

```markdown
1. 修改vim IKAnalyzer.cfg.xml

    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
    <properties>
        <comment>IK Analyzer 扩展配置</comment>
        <!--用户可以在这里配置自己的扩展字典 -->
        <entry key="ext_dict">ext_dict.dic</entry>
         <!--用户可以在这里配置自己的扩展停止词字典-->
        <entry key="ext_stopwords">ext_stopword.dic</entry>
    </properties>

2. 在ik分词器目录下config目录中创建ext_dict.dic文件   编码一定要为UTF-8才能生效
	vim ext_dict.dic 加入扩展词即可

3. 在ik分词器目录下config目录中创建ext_stopword.dic文件 
	vim ext_stopword.dic 加入停用词即可

4.重启es生效
```

> `注意:` **词典的编码必须为UTF-8，否则无法生效!**

## 三、整合应用

### 引入依赖

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
</dependency>
```

### 配置客户端

```java
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {
    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo("127.0.0.1:9200")
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
}
```

### 客户端对象

- ElasticsearchOperations 
- RestHighLevelClient **推荐**

#### ElasticsearchOperations

- 特点: 始终使用面向对象方式操作 ES
  - 索引: 用来存放相似文档集合
  - 映射: 用来决定放入文档的每个字段以什么样方式录入到 ES 中 字段类型 分词器..
  - 文档: 可以被索引最小单元  json 数据格式

##### 相关注解

```java
@Document(indexName = "products", createIndex = true)
public class Product {
		@Id
    private Integer id;
    @Field(type = FieldType.Keyword)
    private String title;
    @Field(type = FieldType.Float)
    private Double price;
    @Field(type = FieldType.Text)
    private String description;
  	//get set ...
}
//1. @Document(indexName = "products", createIndex = true) 用在类上 作用:代表一个对象为一个文档
		-- indexName属性: 创建索引的名称
    -- createIndex属性: 是否创建索引
//2. @Id 用在属性上  作用:将对象id字段与ES中文档的_id对应
//3. @Field(type = FieldType.Keyword) 用在属性上 作用:用来描述属性在ES中存储类型以及分词情况
    -- type: 用来指定字段类型
```

##### 索引文档

```java
 @Test
public void testCreate() throws IOException {
  Product product = new Product();
  product.setId(1); //存在id指定id  不存在id自动生成id
  product.setTitle("怡宝矿泉水");
  product.setPrice(129.11);
  product.setDescription("我们喜欢喝矿泉水....");
  elasticsearchOperations.save(product);
}
```

##### 删除文档

```java
@Test
public void testDelete() {
  Product product = new Product();
  product.setId(1);
  String delete = elasticsearchOperations.delete(product);
  System.out.println(delete);
}
```

##### 查询文档

```java
@Test
public void testGet() {
  Product product = elasticsearchOperations.get("1", Product.class);
  System.out.println(product);
}
```

##### 更新文档

```java
 @Test
public void testUpdate() {
  Product product = new Product();
  product.setId(1);
  product.setTitle("怡宝矿泉水");
  product.setPrice(129.11);
  product.setDescription("我们喜欢喝矿泉水,你们喜欢吗....");
  elasticsearchOperations.save(product);//不存在添加,存在更新
}
```

##### 删除所有

```java
@Test
public void testDeleteAll() {
  elasticsearchOperations.delete(Query.findAll(), Product.class);
}
```

##### 查询所有

```java
@Test
public void testFindAll() {
  SearchHits<Product> productSearchHits = elasticsearchOperations.search(Query.findAll(), Product.class);
  productSearchHits.forEach(productSearchHit -> {
    System.out.println("id: " + productSearchHit.getId());
    System.out.println("score: " + productSearchHit.getScore());
    Product product = productSearchHit.getContent();
    System.out.println("product: " + product);
  });
}
```

### ==RestHighLevelClient==

##### 创建索引映射

```java
 @Test
    public void testCreateIndex() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("fruit");
        createIndexRequest.mapping("{\n" +
                "    \"properties\": {\n" +
                "      \"title\":{\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"price\":{\n" +
                "        \"type\": \"double\"\n" +
                "      },\n" +
                "      \"created_at\":{\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"description\":{\n" +
                "        \"type\": \"text\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" , XContentType.JSON);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse.isAcknowledged());
        restHighLevelClient.close();
    }
```

##### 索引文档

```java
@Test
public void testIndex() throws IOException {
  IndexRequest indexRequest = new IndexRequest("fruit");
  indexRequest.source("{\n" +
                      "          \"id\" : 1,\n" +
                      "          \"title\" : \"蓝月亮\",\n" +
                      "          \"price\" : 123.23,\n" +
                      "          \"description\" : \"这个洗衣液非常不错哦！\"\n" +
                      "        }",XContentType.JSON);
  IndexResponse index = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
  System.out.println(index.status());
}
```

##### 更新文档

```java
@Test
public void testUpdate() throws IOException {
  UpdateRequest updateRequest = new UpdateRequest("fruit","qJ0R9XwBD3J1IW494-Om");
  updateRequest.doc("{\"title\":\"好月亮\"}",XContentType.JSON);
  UpdateResponse update = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
  System.out.println(update.status());
}
```

##### 删除文档

```java
@Test
public void testDelete() throws IOException {
  DeleteRequest deleteRequest = new DeleteRequest("fruit","1");
  DeleteResponse delete = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
  System.out.println(delete.status());
}
```

##### 基于 id 查询文档

```java
@Test
public void testGet() throws IOException {
  GetRequest getRequest = new GetRequest("fruit","1");
  GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
  System.out.println(getResponse.getSourceAsString());
}
```

##### 查询所有

```java
 @Test
public void testSearch() throws IOException {
  SearchRequest searchRequest = new SearchRequest("fruit");
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  sourceBuilder.query(QueryBuilders.matchAllQuery());
  searchRequest.source(sourceBuilder);
  SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  //System.out.println(searchResponse.getHits().getTotalHits().value);
  SearchHit[] hits = searchResponse.getHits().getHits();
  for (SearchHit hit : hits) {
    System.out.println(hit.getSourceAsString());
  }
}
```

##### 综合查询

```java
 @Test
public void testSearch() throws IOException {
  SearchRequest searchRequest = new SearchRequest("fruit");
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  sourceBuilder
    .from(0)
    .size(2)
    .sort("price", SortOrder.DESC)
    .fetchSource(new String[]{"title"},new String[]{})
    .highlighter(new HighlightBuilder().field("description").requireFieldMatch(false).preTags("<span style='color:red;'>").postTags("</span>"))
    .query(QueryBuilders.termQuery("description","错"));
  searchRequest.source(sourceBuilder);
  SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  System.out.println("总条数: "+searchResponse.getHits().getTotalHits().value);
  SearchHit[] hits = searchResponse.getHits().getHits();
  for (SearchHit hit : hits) {
    System.out.println(hit.getSourceAsString());
    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
    highlightFields.forEach((k,v)-> System.out.println("key: "+k + " value: "+v.fragments()[0]));
  }
}
```

## 四、聚合查询

### 简介

聚合：**英文为Aggregation，是es除搜索功能外提供的针对es数据做==统计分析的功能==**。聚合有助于根据搜索查询提供聚合数据。聚合查询是数据库中重要的功能特性，ES作为搜索引擎兼数据库，同样提供了强大的聚合分析能力。它基于查询条件来对数据进行分桶、计算的方法。有点类似于 SQL 中的 group by 再加一些函数方法的操作。

> `注意事项：text类型是不支持聚合的。`

### 测试数据

```json
# 创建索引 index 和映射 mapping
PUT /fruit
{
  "mappings": {
    "properties": {
      "title":{
        "type": "keyword"
      },
      "price":{
        "type":"double"
      },
      "description":{
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}
# 放入测试数据
PUT /fruit/_bulk
{"index":{}}
  {"title" : "面包","price" : 19.9,"description" : "小面包非常好吃"}
{"index":{}}
  {"title" : "旺仔牛奶","price" : 29.9,"description" : "非常好喝"}
{"index":{}}
  {"title" : "日本豆","price" : 19.9,"description" : "日本豆非常好吃"}
{"index":{}}
  {"title" : "小馒头","price" : 19.9,"description" : "小馒头非常好吃"}
{"index":{}}
  {"title" : "大辣片","price" : 39.9,"description" : "大辣片非常好吃"}
{"index":{}}
  {"title" : "透心凉","price" : 9.9,"description" : "透心凉非常好喝"}
{"index":{}}
  {"title" : "小浣熊","price" : 19.9,"description" : "童年的味道"}
{"index":{}}
  {"title" : "海苔","price" : 19.9,"description" : "海的味道"}
```

### 使用

#### 根据某个字段分组

```http
# 根据某个字段进行分组 统计数量
GET /fruit/_search
{
  "query": {
    "term": {
      "description": {
        "value": "好吃"
      }
    }
  }, 
  "aggs": {
    "price_group": {
      "terms": {
        "field": "price"
      }
    }
  }
}
```

#### 求最大值

```http
# 求最大值 
GET /fruit/_search
{
  "aggs": {
    "price_max": {
      "max": {
        "field": "price"
      }
    }
  }
}
```

#### 求最小值

```http
# 求最小值
GET /fruit/_search
{
  "aggs": {
    "price_min": {
      "min": {
        "field": "price"
      }
    }
  }
}
```

#### 求平均值

```http
# 求平均值
GET /fruit/_search
{
  "aggs": {
    "price_agv": {
      "avg": {
        "field": "price"
      }
    }
  }
}
```

#### 求和

```http
# 求和
GET /fruit/_search
{
  "aggs": {
    "price_sum": {
      "sum": {
        "field": "price"
      }
    }
  }
}
```

#### 整合应用

```java
// 求不同价格的数量
@Test
public void testAggsPrice() throws IOException {
  SearchRequest searchRequest = new SearchRequest("fruit");
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  sourceBuilder.aggregation(AggregationBuilders.terms("group_price").field("price"));
  searchRequest.source(sourceBuilder);
  SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  Aggregations aggregations = searchResponse.getAggregations();
  ParsedDoubleTerms terms = aggregations.get("group_price");
  List<? extends Terms.Bucket> buckets = terms.getBuckets();
  for (Terms.Bucket bucket : buckets) {
    System.out.println(bucket.getKey() + ", "+ bucket.getDocCount());
  }
}
```

```java
// 求不同名称的数量
@Test
public void testAggsTitle() throws IOException {
  SearchRequest searchRequest = new SearchRequest("fruit");
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  sourceBuilder.aggregation(AggregationBuilders.terms("group_title").field("title"));
  searchRequest.source(sourceBuilder);
  SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  Aggregations aggregations = searchResponse.getAggregations();
  ParsedStringTerms terms = aggregations.get("group_title");
  List<? extends Terms.Bucket> buckets = terms.getBuckets();
  for (Terms.Bucket bucket : buckets) {
  	System.out.println(bucket.getKey() + ", "+ bucket.getDocCount());
  }
}
```

```java
// 求和
@Test
public void testAggsSum() throws IOException {
  SearchRequest searchRequest = new SearchRequest("fruit");
  SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  sourceBuilder.aggregation(AggregationBuilders.sum("sum_price").field("price"));
  searchRequest.source(sourceBuilder);
  SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
  ParsedSum parsedSum = searchResponse.getAggregations().get("sum_price");
  System.out.println(parsedSum.getValue());
}
```

## 五、集群

### 相关概念

#### 集群<cluster> 

一个集群就是由一个或多个节点组织在一起，`它们共同持有你整个的数据，并一起提供索引和搜索功能`。一个集群由一个唯一的名字标识，这个名字默认就是`elasticsearch`。这个名字是重要的，因为一个节点只能通过指定某个集群的名字，来加入这个集群。

#### 节点<node> 

一个节点是你集群中的一个服务器，作为集群的一部分，它存储你的数据，参与集群的索引和搜索功能。和集群类似，一个节点也是由一个名字来标识的，默认情况下，这个名字是一个随机的漫威漫画角色的名字，这个名字会在启动的时候赋予节点。

#### 索引<Index>

一组相似文档的集合

#### 映射<Mapping>

用来定义索引存储文档的结构如：字段、类型等。

#### 文档<Document>

索引中一条记录,可以被索引的最小单元

#### 分片<shards>

Elasticsearch提供了将索引划分成多份的能力，这些份就叫做分片。当你创建一个索引的时候，你可以指定你想要的分片的数量。每个分片本身也是一个功能完善并且独立的“索引”，这个“索引”可以被放置 到集群中的任何节点上。

#### 复制<replicas>

Index的分片中一份或多份副本。

### 搭建集群

#### 集群规划

```markdown
# 1.准备3个ES节点和一个kibana 节点  ES 9200 9300 
- web: 9201 tcp:9301  node-1  elasticsearch.yml   
- web: 9202 tcp:9302  node-2  elasticsearch.yml
- web: 9203 tcp:9303  node-3  elasticsearch.yml
- kibana: 5602
```

- 注意
  - 所有节点集群名称必须一致     cluster.name
  - 每个节点必须有一个唯一名字 node.name
  - 开启每个节点远程连接  network.host: 0.0.0.0
  - 指定使用 IP地址进行集群节点通信 network.publish_host:
  - 修改 web 端口 tcp 端口 http.port:  transport.tcp.port
  - 指定集群中所有节点通信列表 discovery.seed_hosts: node-1 node-2 node-3 相同
  - 允许集群初始化 master 节点节点数: cluster.initial_master_nodes: ["node-1", "node-2","node-3"]
  - 集群最少几个节点可用 gateway.recover_after_nodes: 2
  - 开启每个节点跨域访问http.cors.enabled: true http.cors.allow-origin: "*"

#### 配置文件

```markdown
# node-1 配置文件
```

```yml
# 指定集群名称 3个节点必须一致
cluster.name: es-cluster
# 指定节点名称 每个节点名字唯一
node.name: node-1
# 开放远程链接
network.host: 0.0.0.0
# 指定使用发布地址进行集群间通信
network.publish_host: 192.168.124.3
# 指定 web 端口
http.port: 9201
# 指定 tcp 端口
transport.tcp.port: 9301
# 指定所有节点的 tcp 通信
discovery.seed_hosts: ["192.168.124.3:9301", "192.168.124.3:9302","192.168.124.3:9303"]
# 指定可以初始化集群的节点名称
cluster.initial_master_nodes: ["node-1", "node-2","node-3"]
# 集群最少几个几点可用
gateway.recover_after_nodes: 2
# 解决跨域问题
http.cors.enabled: true
http.cors.allow-origin: "*"
```

```markdown
# node-2 配置文件
```

```yml
# 指定集群名称 3个节点必须一致
cluster.name: es-cluster
# 指定节点名称 每个节点名字唯一
node.name: node-2
# 开放远程链接
network.host: 0.0.0.0
# 指定使用发布地址进行集群间通信
network.publish_host: 192.168.124.3
# 指定 web 端口
http.port: 9202
# 指定 tcp 端口
transport.tcp.port: 9302
# 指定所有节点的 tcp 通信
discovery.seed_hosts: ["192.168.124.3:9301", "192.168.124.3:9302","192.168.124.3:9303"]
# 指定可以初始化集群的节点名称
cluster.initial_master_nodes: ["node-1", "node-2","node-3"]
# 集群最少几个几点可用
gateway.recover_after_nodes: 2
# 解决跨域问题
http.cors.enabled: true
http.cors.allow-origin: "*"
```

```markdown
# node-3 配置文件
```

```yml
# 指定集群名称 3个节点必须一致
cluster.name: es-cluster
# 指定节点名称 每个节点名字唯一
node.name: node-2
# 开放远程链接
network.host: 0.0.0.0
# 指定使用发布地址进行集群间通信
network.publish_host: 192.168.124.3
# 指定 web 端口
http.port: 9202
# 指定 tcp 端口
transport.tcp.port: 9302
# 指定所有节点的 tcp 通信
discovery.seed_hosts: ["192.168.124.3:9301", "192.168.124.3:9302","192.168.124.3:9303"]
# 指定可以初始化集群的节点名称
cluster.initial_master_nodes: ["node-1", "node-2","node-3"]
# 集群最少几个几点可用
gateway.recover_after_nodes: 2
# 解决跨域问题
http.cors.enabled: true
http.cors.allow-origin: "*"
```

#### 编写 compose 文件

```yml
version: "3.8"
networks:
  escluster:
services:
  es01:
    image: elasticsearch:7.14.0
    ports:
      - "9201:9201"
      - "9301:9301"
    networks:
      - "escluster"
    volumes:
      - ./node-1/data:/usr/share/elasticsearch/data
      - ./node-1/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
      - ./node-1/plugins/ik:/usr/share/elasticsearch/plugins/ik
    environment:
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"

  es02:
    image: elasticsearch:7.14.0
    ports:
      - "9202:9202"
      - "9302:9302"
    networks:
      - "escluster"
    volumes:
      - ./node-2/data:/usr/share/elasticsearch/data
      - ./node-2/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
      - ./node-2/plugins/ik:/usr/share/elasticsearch/plugins/ik
    environment:
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"

  es03:
    image: elasticsearch:7.14.0
    ports:
      - "9203:9203"
      - "9303:9303"
    networks:
      - "escluster"
    volumes:
      - ./node-3/data:/usr/share/elasticsearch/data
      - ./node-3/config/elasticsearch.yml:/usr/share/elasticsearch/config/elasticsearch.yml
      - ./node-3/plugins/ik:/usr/share/elasticsearch/plugins/ik
    environment:
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"

  kibana:
    image: kibana:7.14.0
    ports:
      - "5602:5601"
    networks:
      - "escluster"
    volumes:
      - ./kibana.yml:/usr/share/kibana/config/kibana.yml
```

#### kibana 配置文件

```yml
# kibana配置文件 连接到ES
server.host: "0"
server.shutdownTimeout: "5s"
elasticsearch.hosts: [ "http://192.168.124.3:9201" ] #链接任意节点即可
monitoring.ui.container.elasticsearch.enabled: true
```

#### 查看集群状态

```http
http://10.102.115.3:9200/_cat/health?v
```

### 