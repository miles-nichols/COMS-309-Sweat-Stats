package coms309.workout;

import coms309.workout.Profile.Profile;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;

import coms309.workout.Lifts.Lift;
import coms309.workout.Lifts.LiftRepository;
import coms309.workout.Users.User;
import coms309.workout.Users.UserRepository;




@EnableJpaRepositories
@SpringBootApplication
class WorkoutApplication {

	public static void main(String[] args){
		SpringApplication.run(WorkoutApplication.class, args);
	}

//
//	@Bean
//	CommandLineRunner initUser(LiftRepository liftRepository, UserRepository userRepository) {
//		return args -> {
//			//createUser for easy testing and login
//
//			if(userRepository.findByUsername("a") == null){
//				User user1 = new User("a", "a", "a");
//				Profile p = new Profile();
//				user1.setProfile(p);
//				userRepository.save(user1);
//			}
//			if(userRepository.findByUsername("b") == null){
//				User user2 = new User("b", "b", "b");
//				Profile p = new Profile();
//				user2.setProfile(p);
//				userRepository.save(user2);
//			}
//			if(userRepository.findByUsername("c") == null){
//				User user3 = new User("c", "c", "c");
//				Profile p = new Profile();
//				user3.setProfile(p);
//				userRepository.save(user3);
//			}
//			if(userRepository.findByUsername("test") == null){
//				User user4 = new User("test", "test", "test");
//				Profile p = new Profile();
//				user4.setProfile(p);
//				userRepository.save(user4);
//			}
//		};
//	}
//
//
//
}

