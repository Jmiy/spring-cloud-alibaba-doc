/*
 * Copyright 2015-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.cloud.examples.customer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.Value;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Simple domain class representing a {@link Customer}.
 *
 * @author Oliver Gierke
 */
@Entity
@EqualsAndHashCode(of = "id")
@Getter
@RequiredArgsConstructor
@ToString
@Table(name="customer")
public class Customer implements Serializable {

	//(strategy=GenerationType.AUTO)
	private @Id @GeneratedValue Long id;
	private final String firstname, lastname;

	public Customer() {
		this.firstname = null;
		this.lastname = null;
	}

	public CustomerId getId() {
		return new CustomerId(id);
	}

	@Value
	@Embeddable
	@RequiredArgsConstructor
	@SuppressWarnings("serial")
	public static class CustomerId implements Serializable {

		private final Long customerId;

		public CustomerId() {
			this.customerId = null;
		}
	}

//	@Id
//	@NotNull
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
//	public Provider getProvider() {
//		return provider;
//	}
//
//	public void setProvider(Provider provider) {
//		this.provider = provider;
//	}
//
//	@Id
//	@NotNull
//	@ManyToOne(fetch = FetchType.EAGER)
//	@JoinColumn(name = "service_id", referencedColumnName = "service_id", nullable = false, columnDefinition = "INT UNSIGNED")
//	public Service getService() {
//		return service;
//	}
//
//	public void setService(Service service) {
//		this.service = service;
//	}
}
