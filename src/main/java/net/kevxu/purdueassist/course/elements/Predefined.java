/*
 * Predefined.java
 *
 * The MIT License
 *
 * Copyright (c) 2013 Kaiwen Xu and Rendong Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.kevxu.purdueassist.course.elements;

public interface Predefined {

	public enum Term {
		CURRENT("Current", "CURRENT"),
		FALL2013("Fall 2013", "201410"),
		SUMMER2013("Summer 2013", "201330"),
		SPRING2013("Spring 2013", "201320"),
		FALL2012("Fall 2012", "201310"),
		SUMMER2012("Summer 2012", "201230"),
		SPRING2012("Spring 2012", "201220"),
		FALL2011("Fall 2011", "201210"),
		SUMMER2011("Summer 2011", "201130"),
		SPRING2011("Spring 2011", "201120"),
		FALL2010("Fall 2010", "201110"),
		SUMMER2010("Summer 2010", "201030"),
		SPRING2010("Spring 2010", "201020"),
		FALL2009("Fall 2009", "201010"),
		SUMMER2009("Summer 2009", "200930"),
		SPRING2009("Spring 2009", "200920"),
		FALL2008("Fall 2008", "200910"),
		SUMMER2008("Summer 2008", "200830"),
		SPRING2008("Spring 2008", "200820");

		private String mFullName;
		private String mLinkName;

		Term(String name, String linkName) {
			mFullName = name;
			mLinkName = linkName;
		}

		public String getName() {
			return mFullName;
		}

		public String getLinkName() {
			return mLinkName;
		}

		@Override
		public String toString() {
			return mFullName;
		}
	}

	public enum Subject {
		AAE("Aero & Astro Engineering", "AAE"),
		AAS("African American Studies", "AAS"),
		ABE("Agri & Biol Engineering", "ABE"),
		AD("Art & Design", "AD"),
		AFT("Aerospace Studies", "AFT"),
		AGEC("Agricultural Economics", "AGEC"),
		AGR("Agriculture", "AGR"),
		AGRY("Agronomy", "AGRY"),
		AMST("American Studies", "AMST"),
		ANSC("Animal Sciences", "ANSC"),
		ANTH("Anthropology", "ANTH"),
		ARAB("Arabic", "ARAB"),
		ASAM("Asian American Studies", "ASAM"),
		ASL("American Sign Language", "ASL"),
		ASM("Agricultural Systems Mgmt", "ASM"),
		ASTR("Astronomy", "ASTR"),
		AT("Aviation Technology", "AT"),
		BAND("Bands", "BAND"),
		BCHM("Biochemistry", "BCHM"),
		BCM("Bldg Construct Mgmt Tech", "BCM"),
		BIOL("Biological Sciences", "BIOL"),
		BME("Biomedical Engineering", "BME"),
		BMS("Basic Medical Sciences", "BMS"),
		BTNY("Botany & Plant Pathology", "BTNY"),
		BUS("Business", "BUS"),
		CAND("Candidate", "CAND"),
		CE("Civil Engineering", "CE"),
		CEM("Construction Engr & Mgmt", "CEM"),
		CGT("Computer Graphics Tech", "CGT"),
		CHE("Chemical Engineering", "CHE"),
		CHM("Chemistry", "CHM"),
		CHNS("Chinese", "CHNS"),
		CLCS("Classics", "CLCS"),
		CLPH("Clinical Pharmacy", "CLPH"),
		CMPL("Comparative Literature", "CMPL"),
		CNIT("Computer & Info Tech", "CNIT"),
		COM("Communication", "COM"),
		CPB("Comparative Pathobiology", "CPB"),
		CS("Computer Sciences", "CS"),
		CSR("Consumer ScI & Retailing", "CSR"),
		DANC("Dance", "DANC"),
		EAS("Earth & Atmospheric Sci", "EAS"),
		ECE("Electrical & Computer Engr", "ECE"),
		ECET("Electrical&Comp Engr Tech", "ECET"),
		ECON("Economics", "ECON"),
		EDCI("Educ-Curric & Instruction", "EDCI"),
		EDPS("Educ-Ed'l and Psy Studies", "EDPS"),
		EDST("Ed Leadrship&Cultrl Fnd", "EDST"),
		EEE("Environ & Ecological Engr", "EEE"),
		ENE("Engineering Education", "ENE"),
		ENGL("English", "ENGL"),
		ENGR("First Year Engineering", "ENGR"),
		ENTM("Entomology", "ENTM"),
		ENTR("Entrepreneurship", "ENTR"),
		EPCS("Engr Proj Cmity Service", "EPCS"),
		FLL("Foreign Lang & Literatures", "FLL"),
		FNR("Forestry&Natural Resources", "FNR"),
		FR("French", "FR"),
		FS("Food Science", "FS"),
		FVS("Film And Video Studies", "FVS"),
		GEP("Global Engineering Program", "GEP"),
		GER("German", "GER"),
		GRAD("Graduate Studies", "GRAD"),
		GREK("Greek", "GREK"),
		GS("General Studies", "GS"),
		HDFS("Human Dev &Family Studies", "HDFS"),
		HEBR("Hebrew", "HEBR"),
		HHS("College Health & Human Sci", "HHS"),
		HIST("History", "HIST"),
		HK("Health And Kinesiology", "HK"),
		HONR("Honors", "HONR"),
		HORT("Horticulture", "HORT"),
		HSCI("Health Sciences", "HSCI"),
		HTM("Hospitality & Tourism Mgmt", "HTM"),
		IDE("Interdisciplinary Engr", "IDE"),
		IDIS("Interdisciplinary Studies", "IDIS"),
		IE("Industrial Engineering", "IE"),
		IET("Industrial Engr Technology", "IET"),
		IPPH("Industrial & Phys Pharm", "IPPH"),
		IT("Industrial Technology", "IT"),
		ITAL("Italian", "ITAL"),
		JPNS("Japanese", "JPNS"),
		JWST("Jewish Studies", "JWST"),
		LA("Landscape Architecture", "LA"),
		LALS("Latina Am&Latino Studies", "LALS"),
		LATN("Latin", "LATN"),
		LC("Languages and Cultures", "LC"),
		LCME("Lafayette Center Med Educ", "LCME"),
		LING("Linguistics", "LING"),
		MA("Mathematics", "MA"),
		MARS("Medieval &Renaissance Std", "MARS"),
		MCMP("Med Chem &Molecular Pharm", "MCMP"),
		ME("Mechanical Engineering", "ME"),
		MET("Mechanical Engr Tech", "MET"),
		MFET("Manufacturing Engr Tech", "MFET"),
		MGMT("Management", "MGMT"),
		MSE("Materials Engineering", "MSE"),
		MSL("Military Science & Ldrshp", "MSL"),
		MUS("Music History & Theory", "MUS"),
		NRES("Natural Res & Environ Sci", "NRES"),
		NS("Naval Science", "NS"),
		NUCL("Nuclear Engineering", "NUCL"),
		NUPH("Nuclear Pharmacy", "NUPH"),
		NUR("Nursing", "NUR"),
		NUTR("Nutrition Science", "NUTR"),
		OBHR("Orgnztnl Bhvr &Hum Resrce", "OBHR"),
		OLS("Organiz Ldrshp&Supervision", "OLS"),
		PES("Physical Education Skills", "PES"),
		PHAD("Pharmacy Administration", "PHAD"),
		PHIL("Philosophy", "PHIL"),
		PHPR("Pharmacy Practice", "PHPR"),
		PHRM("Pharmacy", "PHRM"),
		PHYS("Physics", "PHYS"),
		POL("Political Science", "POL"),
		PSY("Psychology", "PSY"),
		PTGS("Portuguese", "PTGS"),
		REG("Reg File Maintenance", "REG"),
		REL("Religious Studies", "REL"),
		RUSS("Russian", "RUSS"),
		SA("Study Abroad", "SA"),
		SCI("General Science", "SCI"),
		SLHS("Speech, Lang&Hear Science", "SLHS"),
		SOC("Sociology", "SOC"),
		SPAN("Spanish", "SPAN"),
		STAT("Statistics", "STAT"),
		TECH("Technology", "TECH"),
		THTR("Theatre", "THTR"),
		USP("Undergrad Studies Prog", "USP"),
		VCS("Veterinary Clinical Sci", "VCS"),
		VM("Veterinary Medicine", "VM"),
		WOST("Women's Studies", "WOST"),
		YDAE("Youth Develop & Ag Educ", "YDAE"),
		CIC("", "CIC"),
		CMCI("", "CMCI"),
		AST("", "AST"),
		CHEM("", "CHEM"),
		CSCI("", "CSCI"),
		COMM("", "COMM"),
		ENG("", "ENG"),
		GEOL("", "GEOL"),
		LSTU("", "LSTU"),
		FINA("", "FINA"),
		SPCH("", "SPCH"),
		INFO("", "INFO"),
		MATH("", "MATH"),
		CMCL("", "CMCL"),
		GEOG("", "GEOG"),
		JOUR("", "JOUR"),
		COAS("", "COAS"),
		HPER("", "HPER"),
		HSRV("", "HSRV"),
		POLS("", "POLS"),
		SPEA("", "SPEA"),
		TEL("", "TEL"),
		CIT("", "CIT"),
		EALC("", "EALC"),
		SWK("", "SWK"),
		ANAT("", "ANAT"),
		CJUS("", "CJUS"),
		PHYT("", "PHYT"),
		PMTD("", "PMTD"),
		DRAF("", "DRAF"),
		PRDM("", "PRDM"),
		SUPV("", "SUPV"),
		ERTH("", "ERTH"),
		FOLK("", "FOLK"),
		CMLT("", "CMLT"),
		OADM("", "OADM"),
		NMCM("", "NMCM"),
		PHSL("", "PHSL");

		private String mFullName;
		private String mLinkName;

		Subject(String fullName, String linkName) {
			mFullName = fullName;
			mLinkName = linkName;
		}

		public String getName() {
			return mFullName;
		}

		public String getLinkName() {
			return mLinkName;
		}

		@Override
		public String toString() {
			return mLinkName;
		}
	}

	public enum Type {
		DistanceLearning("Distance Learning", "DIS"),
		IndividualStudy("Individual Study", ""),
		Laboratory("Laboratory", "LAB"),
		Lecture("Lecture", "LEC"),
		Recitation("Recitation", "REC"),
		PracticeStudyObservation("Practice Study Observation", "PSO"),
		LaboratoryPreparation("Laboratory Preparation", ""),
		Experiential("Experiential", ""),
		Research("Research", ""),
		Studio("Studio", ""),
		Lab1("Lab1", ""),
		Clinic("Clinic", ""),
		Lecture1("Lecture1", ""),
		Presentation("Presentation", ""),
		TravelTime("TravelTime", ""),
		Experiential1("Experiential1", ""),
		Clinic1("Clinic1", ""),
		Clinic2("Clinic2", ""),
		Clinic3("Clinic3", ""),
		Studio1("Studio1", "");

		private String mFullName;
		private String mLinkName;

		Type(String name, String linkName) {
			mFullName = name;
			mLinkName = linkName;
		}

		public String getName() {
			return mFullName;
		}

		public String getLinkName() {
			return mLinkName;
		}

		@Override
		public String toString() {
			return mFullName;
		}
	}

	public enum Level {
		Undergraduate("Undergraduate", "UG"),
		Graduate("Graduate", "GR"),
		Professional("Professional", "PR"),
		IndianaCollegeNetwork("Indiana College Network", "IC");

		private String mFullName;
		private String mLinkName;

		Level(String fullName, String linkName) {
			mFullName = fullName;
			mLinkName = linkName;
		}

		public String getName() {
			return mFullName;
		}

		public String getLinkName() {
			return mLinkName;
		}

		@Override
		public String toString() {
			return mFullName;
		}
	}
}
