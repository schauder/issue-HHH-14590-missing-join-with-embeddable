/*
 * Copyright 2021 the original author or authors.
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
package org.hibernate.bugs;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The same as {@link org.hibernate.bugs.PlainEmbeddable} but with column annotations specifying a table.
 */
@Embeddable
public class SecondTableEmbeddable {
	@Column(table = "secondary")
	String secName;
	@Column(table = "secondary")
	String secValue;
}
