class Card:
    def __init__(self, name, cost, resource_type, effect_function, description=""):
        self.name = name
        self.cost = cost
        self.resource_type = resource_type
        self.effect_function = effect_function
        self.description = description
