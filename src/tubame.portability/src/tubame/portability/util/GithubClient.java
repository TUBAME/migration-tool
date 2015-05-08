package tubame.portability.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipInputStream;

import javax.net.ssl.HttpsURLConnection;

public class GithubClient {

	static final int CONNECT_TIMEOUT_MILLIS = 15 * 1000; // 15s
	static final int READ_TIMEOUT_MILLIS = 20 * 1000; // 20s
	static final int CHUNK_SIZE = 4096;
	
	
	/**
     * The type that defines the type of service.<br/>
     */
    public enum SUPPORTED_CONTENT {
    	
    	ZIP("application/zip;");
        
        private String contentType;
      

        private SUPPORTED_CONTENT(String contentType) {
        	this.contentType = contentType;
        }

		public String getContentType() {
			return this.contentType;
		}
        
    }
    
	HttpsURLConnection openConnection(String endpoint) throws IOException {
		HttpsURLConnection connection = (HttpsURLConnection) new URL(endpoint).openConnection();
		connection.setConnectTimeout(CONNECT_TIMEOUT_MILLIS);
		connection.setReadTimeout(READ_TIMEOUT_MILLIS);
		return connection;
	}
	
	
    void prepareGetRequest(HttpsURLConnection connection,SUPPORTED_CONTENT contentType) throws IOException {
        connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "TUBAME");
        connection.setDoOutput(true);
    }
    
    InputStream readResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();
        if (reason == null) reason = ""; // HttpURLConnection treats empty reason as null.
        InputStream stream;

        if (status >= 400) {
            throw new IOException("http status="+status+",reason="+reason);
        } else {
        	return new BufferedInputStream(connection.getInputStream());
        }
    }
    
    public GithubClientResponce execute(String endpointUrl,SUPPORTED_CONTENT contentType) throws IOException {
        try {
            HttpsURLConnection connection = openConnection(endpointUrl);
            prepareGetRequest(connection,contentType);
            return new GithubClientResponce(readResponse(connection),contentType);
        } catch (IOException e) {
            throw e;
        }
    }
    
    public class GithubClientResponce{
    	private InputStream inputStream;
		private SUPPORTED_CONTENT contentType;

		public GithubClientResponce(InputStream inputStream,SUPPORTED_CONTENT contentType) {
			super();
			this.inputStream = inputStream;
			this.contentType = contentType;
		}
		
		public InputStream getInputStream(){
			if(contentType.getContentType().equals(SUPPORTED_CONTENT.ZIP.getContentType())){
				return new ZipInputStream(this.inputStream);
			}
			return null;
		}
    	
    	
    }
}
