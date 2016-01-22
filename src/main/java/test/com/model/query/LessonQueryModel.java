package test.com.model.query;

import java.util.Date;

import test.com.entity.Lesson.LessonStatus;

public class LessonQueryModel{
	private static final long serialVersionUID = 9041135804381319534L;

	private Long userIdEQ;
	private Long categoryIdEQ;
	private LessonStatus lessonStatusEQ;
	private String titleLK;
	private Date createdTimeGTE;
	private Date createdTimeLT;
	private Long[] idIN;
	
	public Long[] getIdIN() {
		return idIN;
	}

	public void setIdIN(Long[] idIN) {
		this.idIN = idIN;
	}

	public Long getUserIdEQ() {
		return userIdEQ;
	}

	public void setUserIdEQ(Long userIdEQ) {
		this.userIdEQ = userIdEQ;
	}

	public Long getCategoryIdEQ() {
		return categoryIdEQ;
	}

	public void setCategoryIdEQ(Long categoryIdEQ) {
		this.categoryIdEQ = categoryIdEQ;
	}

	public LessonStatus getLessonStatusEQ() {
		return lessonStatusEQ;
	}

	public void setLessonStatusEQ(LessonStatus lessonStatusEQ) {
		this.lessonStatusEQ = lessonStatusEQ;
	}

	public String getTitleLK() {
		return titleLK;
	}

	public void setTitleLK(String titleLK) {
		this.titleLK = titleLK;
	}

	public Date getCreatedTimeGTE() {
		return createdTimeGTE;
	}

	public void setCreatedTimeGTE(Date createdTimeGTE) {
		this.createdTimeGTE = createdTimeGTE;
	}

	public Date getCreatedTimeLT() {
		return createdTimeLT;
	}

	public void setCreatedTimeLT(Date createdTimeLT) {
		this.createdTimeLT = createdTimeLT;
	}

}
