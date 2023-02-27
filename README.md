# blogSystem
This is a project about a blogSystem written in Java.

# 一、项目介绍：
基于SpringBoot+Mybatis+Mysql+Redis+kafka+Elasticsearch的多人博客系统，实现了用户登录、文章查看、文章评
论、文章搜索、私聊、热门帖子等功能。
<ol>
<li>文章搜索使用Elasticsearch来实现，搜索方式使用的multi match，分词器使用的IK分词插件。</li>
<li>计算帖子分数的方式是使用ScheduledThreadPool周期计算新建帖子、平路、点赞帖子的分数，分数主要和帖子时间和点赞评论
数有关。统计这部分帖子的方法是将其加入到redis中，然后周期性读取。</li>
<li>kafka主要用于异步将新建的帖子加入到ElasticSearch中进行全文索引，提高网页响应速度。</li>
<li>redis用来缓存热门帖子，缓存登录用户的登陆凭证，提高程序的性能</li>
<li>通过MD5对用户密码密文存储，考虑到拖库的风险性，对密码加盐来防止拖库造成大的影响，使用Smtp协议实现邮箱验证功能，
使用kaptcha进行图片验证。</li>
<li>通过字典树实现敏感词过滤</li>
</ol>
# 二、使用方法
<ol>
  <li>把代码git到本地</li>
  <li>在IDEA中导入maven项目</li>
  <li>确保IDEA本地maven仓库配置正确</li>
  <li>本机确保有mysql、redis、kafka、es环境</li>
  <li>分别启动mysql、redis、kafka</li>
  <li>kafka的配置文件group-id要改成application.properties中相同的名字</li>
  <li>编译项目，需要下载很多依赖，可能比较慢</li>
  <li>运行项目，此时浏览器可以访问8080端口了</li>
</ol>
