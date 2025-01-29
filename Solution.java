#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <jansson.h>  // Requires Jansson JSON library

// Function to convert a number from a given base to decimal
int convert_to_decimal(const char *value, int base) {
    return strtol(value, NULL, base);
}

// Function to compute Lagrange Interpolation and get constant term c
double lagrange_interpolation(int x[], int y[], int n) {
    double c = 0.0;
    
    for (int i = 0; i < n; i++) {
        double term = y[i];
        
        for (int j = 0; j < n; j++) {
            if (i != j) {
                term *= (0.0 - x[j]) / (x[i] - x[j]);
            }
        }
        
        c += term;
    }
    
    return c;
}

int main() {
    // Sample JSON Input (should be read from a file or input stream)
    const char *json_text = "{\"keys\": {\"n\": 4, \"k\": 3}, "
                            "\"1\": {\"base\": \"10\", \"value\": \"4\"}, "
                            "\"2\": {\"base\": \"2\", \"value\": \"111\"}, "
                            "\"3\": {\"base\": \"10\", \"value\": \"12\"}, "
                            "\"6\": {\"base\": \"4\", \"value\": \"213\"}}";

    json_t *root;
    json_error_t error;
    root = json_loads(json_text, 0, &error);
    
    if (!root) {
        fprintf(stderr, "Error parsing JSON: %s\n", error.text);
        return 1;
    }

    json_t *keys = json_object_get(root, "keys");
    int n = json_integer_value(json_object_get(keys, "n"));
    int k = json_integer_value(json_object_get(keys, "k"));

    int x[n], y[n], count = 0;
    
    // Extract (x, y) pairs from JSON
    const char *key;
    json_t *value;
    json_object_foreach(root, key, value) {
        if (strcmp(key, "keys") == 0) continue; // Skip "keys" object

        int x_value = atoi(key);
        int base = atoi(json_string_value(json_object_get(value, "base")));
        const char *y_value = json_string_value(json_object_get(value, "value"));
        
        x[count] = x_value;
        y[count] = convert_to_decimal(y_value, base);
        count++;
    }

    json_decref(root); // Free JSON memory

    // Ensure we have enough points for interpolation
    if (count < k) {
        fprintf(stderr, "Not enough points to determine the polynomial.\n");
        return 1;
    }

    // Compute the constant term c using Lagrange Interpolation
    double c = lagrange_interpolation(x, y, k);

    printf("Constant term (c): %.2lf\n", c);

    return 0;
}