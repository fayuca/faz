package com.example.faz.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public class PageResponse<T> {
	private List<T> content;
	private int number;
	private int size;
	private long totalElements;
	private int totalPages;

	public PageResponse(List<T> content, int number, int size, long totalElements, int totalPages) {
		this.content = content;
		this.number = number;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
	}

	public List<T> getContent() {
		return content;
	}

	public void setContent(List<T> content) {
		this.content = content;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int page) {
		this.number = page;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public long getTotalElements() {
		return totalElements;
	}

	public void setTotalElements(long totalElements) {
		this.totalElements = totalElements;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public static <T> PageResponse<T> from(Page<T> page) {
		return new PageResponse<>(
				page.getContent(),
				page.getNumber(),
				page.getSize(),
				page.getTotalElements(),
				page.getTotalPages());
	}
}
