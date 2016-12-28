class PointsAndScores implements Comparable<PointsAndScores> {

    int score;
    Positions point;

    PointsAndScores(int score, Positions point) {
        this.score = score;
        this.point = point;
    }

	@Override
	public int compareTo(PointsAndScores o) {
		// TODO Auto-generated method stub
		return Integer.compare(this.score, o.score);
	}
}