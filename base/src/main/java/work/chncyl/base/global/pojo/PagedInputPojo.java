package work.chncyl.base.global.pojo;

/**
 * 实现功能描述:
 * 分页dto类型，分页参数继承此类
 */
public class PagedInputPojo {
    //    每页的数量
    private Integer pageSize;
    //    页码
    private Integer currentPage;

    public Integer getPageSize() {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        if (currentPage == null || currentPage <= 0) {
            currentPage = 1;
        }
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        if (currentPage == null || currentPage <= 0) {
            currentPage = 1;
        }
        this.currentPage = currentPage;
    }
}
