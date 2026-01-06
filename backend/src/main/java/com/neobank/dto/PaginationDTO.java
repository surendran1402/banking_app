package com.neobank.dto;

public class PaginationDTO {
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PaginationDTO() {}

    public static PaginationDTOBuilder builder() { return new PaginationDTOBuilder(); }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalItems() { return totalItems; }
    public void setTotalItems(long totalItems) { this.totalItems = totalItems; }

    public static class PaginationDTOBuilder {
        private final PaginationDTO dto = new PaginationDTO();

        public PaginationDTOBuilder currentPage(int currentPage) { dto.setCurrentPage(currentPage); return this; }
        public PaginationDTOBuilder totalPages(int totalPages) { dto.setTotalPages(totalPages); return this; }
        public PaginationDTOBuilder totalItems(long totalItems) { dto.setTotalItems(totalItems); return this; }

        public PaginationDTO build() { return dto; }
    }
}
