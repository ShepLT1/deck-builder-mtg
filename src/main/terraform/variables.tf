variable "aws_region" {
  default = "us-west-2"
}

variable "vpc_cidr_block" {
  description = "CIDR block for VPC"
  type = string
  default = "10.0.0.0/16"
}

variable "subnet_count" {
  description = "Number of subnets"
  type = map(number)
  default = {
    public = 2,
    private = 2
  }
}

variable "settings" {
  description = "Configuration settings"
  type = map(any)
  default = {
    "database" = {
      allocated_storage = 10
      engine = "postgres"
      engine_version = "15.5"
      instance_class = "db.t3.micro"
      db_name = "mtg_deck_builder"
      port = 5432
      skip_final_snapshot = true
    },
    "web_app" = {
      count = 1
      instance_type = "t2.micro"
      volume_size = 10
      volume_type = "gp2"
      max_size = 2
      min_size = 1
      desired_capacity = 1
      health_check_grace_period = 0
      health_check_type = "EC2"
      protect_from_scale_in = false
    }
  }
}

variable "public_subnet_cidr_blocks" {
  description = "Available CIDR blocks for public subnets"
  type = list(string)
    default = [
      "10.0.1.0/24",
      "10.0.2.0/24",
      "10.0.3.0/24",
      "10.0.4.0/24"
    ]
}

variable "private_subnet_cidr_blocks" {
  description = "Available CIDR blocks for private subnets"
  type = list(string)
    default = [
      "10.0.101.0/24",
      "10.0.102.0/24",
      "10.0.103.0/24",
      "10.0.104.0/24"
    ]
}

variable "domain_name" {
  description = "Domain name for this application"
  type = string
  default = "digitaldecksmtg.com"
}

// for setting up ssh from my machine
variable "my_ip" {
  description = "My IP address"
  type = string
  sensitive = true
}

variable "db_username" {
  description = "DB master user"
  type = string
  sensitive = true
}

variable "db_password" {
  description = "DB master user password"
  type = string
  sensitive = true
}