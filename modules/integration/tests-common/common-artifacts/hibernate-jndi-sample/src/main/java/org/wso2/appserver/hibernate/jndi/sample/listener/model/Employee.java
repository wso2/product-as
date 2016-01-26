package org.wso2.appserver.hibernate.jndi.sample.listener.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="Employee", uniqueConstraints={@UniqueConstraint(columnNames={"ID"})})
public class Employee {

	@Id
	@Column(name="ID", nullable=false, unique=true)
	@GeneratedValue(strategy= GenerationType.AUTO)
	private int id;

	@Column(name="NAME", length=20, nullable=true)
	private String name;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
