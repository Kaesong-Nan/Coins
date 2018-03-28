package de.max_overlack.coins;

public interface MySQLResponseReceiveListener<ResponseType> {
	
	public void onMySQLResponseReceived(ResponseType result);
}
