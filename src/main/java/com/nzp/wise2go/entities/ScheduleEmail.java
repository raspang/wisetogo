package com.nzp.wise2go.entities;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ScheduleEmail {

		@Email
	    @NotEmpty
	    private String email;

	    @NotEmpty
	    private String subject;

	    @NotEmpty
	    private String body;

	    @NotNull
	    private LocalDateTime dateTime;

	    @NotNull
	    private ZoneId timeZone;
	    
	    @NotNull
	    private File file;

	    public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getSubject() {
	        return subject;
	    }

	    public void setSubject(String subject) {
	        this.subject = subject;
	    }

	    public String getBody() {
	        return body;
	    }

	    public void setBody(String body) {
	        this.body = body;
	    }

	    public LocalDateTime getDateTime() {
	        return dateTime;
	    }

	    public void setDateTime(LocalDateTime dateTime) {
	        this.dateTime = dateTime;
	    }

	    public ZoneId getTimeZone() {
	        return timeZone;
	    }

	    public void setTimeZone(ZoneId timeZone) {
	        this.timeZone = timeZone;
	    }

		public File getFile() {
			return file;
		}

		public void setFile(File file) {
			this.file = file;
		}
	
	    
}
