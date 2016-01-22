package test.com.model.query;


public class LearnedLessonQueryModel {
	private static final long serialVersionUID = 9041135804381319534L;

	private Long userIdEQ;
	private Long[] lessonIdIN;
	public Long getUserIdEQ() {
		return userIdEQ;
	}
	public void setUserIdEQ(Long userIdEQ) {
		this.userIdEQ = userIdEQ;
	}
	public Long[] getLessonIdIN() {
		return lessonIdIN;
	}
	public void setLessonIdIN(Long[] lessonIdIN) {
		this.lessonIdIN = lessonIdIN;
	}

}
