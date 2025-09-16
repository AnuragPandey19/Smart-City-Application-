# 🏙️ Smart City Application

An interactive **Java Swing + MySQL desktop application** that serves as a **digital city guide**.  
The application helps users explore essential city services, including hotels, restaurants, shopping malls, tourist attractions, and transportation options.  

## ✨ Features

- **City Information**: Overview of major landmarks and facilities.  
- **Tourism Guide**: Browse hotels, restaurants, and tourist places by location.  
- **Transportation System**: Search and book transport options using pickup and destination, with a cab-like booking interface.  
- **Shopping Malls**: Search malls by location and view details such as opening and closing times.  
- **User-Friendly UI**: Clean Java Swing design for smooth navigation and data interaction.  

## 🛠️ Tech Stack

- **Programming Language**: Java  
- **UI Framework**: Swing, AWT  
- **Database**: MySQL with JDBC  
- **IDE Used**: IntelliJ IDEA Community Edition  
- **Libraries**: MySQL Connector/J  
- **OS Compatibility**: Windows / Linux  

## 📂 Project Directory Structure
<pre>
Smart City/
│── .idea/
│ ├── .gitignore
│ ├── misc.xml
│ ├── modules.xml
│ └── workspace.xml
│
│── bin/com/smartcity/
│
│── lib/
│ └── mysql-connector-j-9.2.0.jar
│
│── out/production/Smart City/com/smartcity/
│ ├── CityInfo.class
│ ├── DBConnection.class
│ ├── HomePage.class
│ ├── Hotels.class
│ ├── Main.class
│ ├── MainFrame.class
│ ├── Restaurants.class
│ ├── ShoppingMalls.class
│ ├── SignUp.class
│ ├── Tourism.class
│ ├── TouristPlaces.class
│ ├── Transportation.class
│ └── Welcome.class
│
│── src/
│
│── .gitignore
│── manifest.mf
│── Smart City.iml
</pre>


## 🗄️ Database Schema (MySQL)

- **hotels** → hotel name, location, contact, price range, rating  
- **restaurants** → restaurant name, location, cuisine type, rating  
- **tourist_places** → place name, location, type, timings  
- **transportation** → transport type, fare, pickup, destination, booking method  
- **shopping_mall** → mall name, location, opening time, closing time  

## 🚀 Getting Started

### Prerequisites
- Java JDK (8 or above)  
- MySQL Server  
- IntelliJ IDEA (or any Java IDE)  
- MySQL Connector/J  


Configure the MySQL database:

Create database smart_city

Import provided SQL schema & tables.

Update DB credentials in DBConnection.java.

Run Main.java to start the application.

## 📊 Flow Chart

<img width="3840" height="1456" alt="Flow Chart" src="https://github.com/user-attachments/assets/b997eee3-c0c5-4838-85d5-fba42b6ae61a" />

## 🎥 Demo Video

You can watch the demo video on [Demo Video](https://www.linkedin.com/posts/anurag-pandey-154259280_java-oop-softwaredevelopment-activity-7313682593965584385-CY9F?utm_source=social_share_video_v2&utm_medium=android_app&rcm=ACoAAERhapABdHnSmcXmj4dG1UIe-IwLGgBL0sM&utm_campaign=share_via).



## 🔮 Future Enhancements
Real-time updates & GPS integration

User authentication system

Advanced search filters

Real-time transport booking
