package fm.douban.param;

import fm.douban.model.User;

public class UserQueryParam extends User {
    //页码号，从1开始计数。值为1表示第一页。默认第一页。
    private int pageNum = 1;
    //每页记录数，默认10条。
    private int pageSize = 10;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


}
