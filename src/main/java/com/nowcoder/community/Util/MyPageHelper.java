package com.nowcoder.community.Util;

/**
 * @author fulianpeng
 * @create com.nowcoder.community.Util-2022-07-29 22:46
 */
public class MyPageHelper {
    private int currentPage=1;//前端传过来

    private int limit=10;//前端传进来
    private String url;//controller给的
    private int totalPage;//计算出来
    private int totalRow;//mysql给的
    private int from;//计算
    private int to;//计算
    private int offset;//计算
    public void setCurrentPage(int currentPage) {
        if(currentPage<=0)//小于等于0就置为1
            this.currentPage = 1;
        else
            this.currentPage = currentPage;
    }
    public void setLimit(int limit){
        if(limit>100) limit=10;//每页条目不要太多
        this.limit=limit;
    }

    public void setTotalRow(int totalRow){
        this.totalPage=totalRow%limit==0?
                totalRow/limit:totalRow/limit+1;
        if(currentPage>totalPage) this.currentPage=totalPage==0?1:totalPage;//对页码超过总页数进行限制
        this.totalRow = totalRow;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getLimit() {
        return limit;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getTotalRow() {
        return totalRow;
    }

    public String getUrl() {
        return url;
    }

    public int getFrom() {
        return currentPage-2<=0?1:currentPage-2;
    }

    public int getTo() {
        return currentPage+2>totalPage?totalPage:currentPage+2;
    }

    public int getOffset(){
        return (currentPage-1)*limit;
    }
}
