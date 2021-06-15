Nginx

1, 安装配置

> 1, 下载解压
>
> 2,  ./configure 
>
> 可能需要环境配置
>
> yum install gcc-c++
>
> yum install -y pcre pcre-devel
>
> yum install -y zlib zlib-devel
>
> yum install -y openssl openssl-devel
>
> 3, make 
>
> 4, make install
>
> 5, 启动

2, 选择配置/etc/profile, 省略

3, 启动,重启

>1, 启动 nginx
>
>2, 停止, nginx -s stop      fast shutdown
>
>3, 停止 nginx -s quit        graceful shutdown 
>
>4,重启  nginx -s reload 
>
>5, reopen??? 
>
>

